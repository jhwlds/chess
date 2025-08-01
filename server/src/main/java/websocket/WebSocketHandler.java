package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MySQLGameDAO;
import dataaccess.AuthTokenDAO;
import dataaccess.MySQLAuthTokenDAO;
import model.GameData;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private final GameDAO gameDAO = new MySQLGameDAO();
    private final AuthTokenDAO authDAO = new MySQLAuthTokenDAO();

    public static class Connection {
        public String authToken;
        public Session session;
        public Integer gameID;

        public Connection(String authToken, Session session, Integer gameID) {
            this.authToken = authToken;
            this.session = session;
            this.gameID = gameID;
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(session, command);
                case MAKE_MOVE -> makeMove(session, command);
                case LEAVE -> leave(session, command);
                case RESIGN -> resign(session, command);
            }
        } catch (Exception e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void connect(Session session, UserGameCommand command) throws IOException {
        try {
            String authToken = command.getAuthToken();
            Integer gameID = command.getGameID();

            // Validate authToken
            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                sendError(session, "Error: Invalid auth token");
                return;
            }

            // Validate gameID
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                sendError(session, "Error: Game not found");
                return;
            }

            Connection connection = new Connection(authToken, session, gameID);
            connections.put(authToken, connection);

            ChessGame game = gameData.game();
            if (game == null) {
                game = new ChessGame();
            }
            LoadGameMessage loadGameMessage = new LoadGameMessage(game);
            sendMessage(session, loadGameMessage);

            sendNotificationToOthers(authToken, gameID, "A player joined the game");

        } catch (DataAccessException e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void makeMove(Session session, UserGameCommand command) throws IOException {
        try {
            String authToken = command.getAuthToken();
            Integer gameID = command.getGameID();
            ChessMove move = command.getMove();
            
            if (move == null) {
                sendError(session, "Error: No move provided");
                return;
            }

            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                sendError(session, "Error: Invalid auth token");
                return;
            }

            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                sendError(session, "Error: Game not found");
                return;
            }

            ChessGame game = gameData.game();
            if (game == null) {
                game = new ChessGame();
            }

            ChessGame.TeamColor currentTurn = game.getTeamTurn();
            ChessPosition startPosition = move.getStartPosition();
            ChessPiece piece = game.getBoard().getPiece(startPosition);
            
            if (piece == null) {
                sendError(session, "Error: No piece at start position");
                return;
            }

            ChessGame.TeamColor pieceColor = piece.getTeamColor();
            String playerUsername = authData.username();

            ChessGame.TeamColor playerColor = null;
            if (playerUsername.equals(gameData.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if (playerUsername.equals(gameData.blackUsername())) {
                playerColor = ChessGame.TeamColor.BLACK;
            }
            
            if (playerColor == null) {
                sendError(session, "Error: Player not in game");
                return;
            }
            
            if (pieceColor != playerColor) {
                sendError(session, "Error: Cannot move opponent's piece");
                return;
            }
            
            if (currentTurn != playerColor) {
                sendError(session, "Error: Not your turn");
                return;
            }
            
            try {
                game.makeMove(move);
            } catch (chess.InvalidMoveException e) {
                sendError(session, "Error: " + e.getMessage());
                return;
            }

            GameData updatedGameData = new GameData(gameData.gameID(), gameData.gameName(),
                                                   gameData.whiteUsername(), gameData.blackUsername(), game);
            gameDAO.updateGame(updatedGameData);

            LoadGameMessage loadGameMessage = new LoadGameMessage(game);
            for (Connection connection : connections.values()) {
                if (connection.gameID.equals(gameID)) {
                    sendMessage(connection.session, loadGameMessage);
                }
            }

            sendNotificationToOthers(authToken, gameID, authData.username() + " made a move");

        } catch (DataAccessException e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void leave(Session session, UserGameCommand command) throws IOException {
        // TODO: Implement leave logic
        sendError(session, "Leave not implemented yet");
    }

    private void resign(Session session, UserGameCommand command) throws IOException {
        // TODO: Implement resign logic
        sendError(session, "Resign not implemented yet");
    }

    private void sendMessage(Session session, Object message) throws IOException {
        session.getRemote().sendString(gson.toJson(message));
    }

    private void sendError(Session session, String errorMessage) throws IOException {
        ErrorMessage error = new ErrorMessage(errorMessage);
        sendMessage(session, error);
    }

    private void sendNotificationToOthers(String excludeAuthToken, Integer gameID, String message) throws IOException {
        NotificationMessage notification = new NotificationMessage(message);
        for (Connection connection : connections.values()) {
            if (!connection.authToken.equals(excludeAuthToken) && connection.gameID.equals(gameID)) {
                sendMessage(connection.session, notification);
            }
        }
    }
} 