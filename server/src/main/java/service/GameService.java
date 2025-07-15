package service;

import dataaccess.DAOFactory;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import shared.CreateGameRequest;
import shared.CreateGameResult;

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
}
