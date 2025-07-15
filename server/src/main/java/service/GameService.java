package service;

import chess.ChessGame;
import dataaccess.DAOFactory;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import shared.*;

import java.util.ArrayList;
import java.util.List;

public class GameService {
    public CreateGameResult createGame(String authToken, CreateGameRequest request) {
        try {
            if (authToken == null || authToken.isEmpty() || request == null || request.gameName() == null) {
                return new CreateGameResult("Error: bad request");
            }

            AuthData auth = DAOFactory.authDAO().getAuth(authToken);
            if (auth == null) {
                return new CreateGameResult("Error: unauthorized");
            }

            GameData newGame = new GameData(0, request.gameName(), null, null, null);
            int gameID = DAOFactory.gameDAO().insertGame(newGame);

            return new CreateGameResult(gameID, null);
        } catch (DataAccessException e) {
            return new CreateGameResult("Error: " + e.getMessage());
        }
    }

    public JoinGameResult joinGame(String authToken, JoinGameRequest req) {
        try {
            if (authToken == null || req == null || req.gameID() == null) {
                return new JoinGameResult("Error: bad request");
            }

            var auth = DAOFactory.authDAO().getAuth(authToken);
            if (auth == null) {
                return new JoinGameResult("Error: unauthorized");
            }

            var game = DAOFactory.gameDAO().getGame(req.gameID());
            if (game == null) {
                return new JoinGameResult("Error: bad request");
            }

            String username = auth.username();
            var color = req.playerColor();

            if (color == null) {
                return new JoinGameResult("Error: bad request");
            }

            if (color == ChessGame.TeamColor.WHITE) {
                if (game.whiteUsername() != null) return new JoinGameResult("Error: already taken");
                game = game.withWhiteUsername(username);
            } else if (color == ChessGame.TeamColor.BLACK) {
                if (game.blackUsername() != null) return new JoinGameResult("Error: already taken");
                game = game.withBlackUsername(username);
            }

            DAOFactory.gameDAO().updateGame(game);
            return new JoinGameResult(null);
        } catch (DataAccessException e) {
            return new JoinGameResult("Error: " + e.getMessage());
        }
    }
}
