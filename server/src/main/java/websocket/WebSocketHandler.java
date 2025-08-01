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

    private AuthData validateAuthToken(Session session, String authToken) throws IOException, DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            sendError(session, "Error: Invalid auth token");
            return null;
        }
        return authData;
    }

    private GameData validateGame(Session session, Integer gameID) throws IOException, DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            sendError(session, "Error: Game not found");
            return null;
        }
        return gameData;
    }

    private ChessGame getGameWithFallback(GameData gameData) {
        ChessGame game = gameData.game();
        if (game == null) {
            game = new ChessGame();
        }
        return game;
    }

    private void updateGameInDatabase(GameData gameData, ChessGame game) throws DataAccessException {
        GameData updatedGameData = new GameData(gameData.gameID(), gameData.gameName(),
                gameData.whiteUsername(), gameData.blackUsername(), game);
        gameDAO.updateGame(updatedGameData);
    }

    private void executeWithValidation(Session session, UserGameCommand command,
                                       ValidationHandler handler) throws IOException {
        try {
            String authToken = command.getAuthToken();
            Integer gameID = command.getGameID();

            AuthData authData = validateAuthToken(session, authToken);
            if (authData == null) return;

            GameData gameData = validateGame(session, gameID);
            if (gameData == null) return;

            handler.handle(session, authToken, gameID, authData, gameData);

        } catch (DataAccessException e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    @FunctionalInterface
    private interface ValidationHandler {
        void handle(Session session, String authToken, Integer gameID,
                    AuthData authData, GameData gameData) throws IOException, DataAccessException;
    }

    private void connect(Session session, UserGameCommand command) throws IOException {
        executeWithValidation(session, command, (session1, authToken, gameID, authData, gameData) -> {
            Connection connection = new Connection(authToken, session1, gameID);
            connections.put(authToken, connection);

            ChessGame game = getGameWithFallback(gameData);
            LoadGameMessage loadGameMessage = new LoadGameMessage(game);
            sendMessage(session1, loadGameMessage);

            sendNotificationToOthers(authToken, gameID, "A player joined the game");
        });
    }

    private void makeMove(Session session, UserGameCommand command) throws IOException {
        executeWithValidation(session, command, (session1, authToken, gameID, authData, gameData) -> {
            ChessMove move = command.getMove();

            if (move == null) {
                sendError(session1, "Error: No move provided");
                return;
            }

            ChessGame game = getGameWithFallback(gameData);
            
            if (game.isGameOver()) {
                sendError(session1, "Error: Game is over");
                return;
            }

            ChessGame.TeamColor currentTurn = game.getTeamTurn();
            ChessPosition startPosition = move.getStartPosition();
            ChessPiece piece = game.getBoard().getPiece(startPosition);

            if (piece == null) {
                sendError(session1, "Error: No piece at start position");
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
                sendError(session1, "Error: Player not in game");
                return;
            }

            if (pieceColor != playerColor) {
                sendError(session1, "Error: Cannot move opponent's piece");
                return;
            }

            if (currentTurn != playerColor) {
                sendError(session1, "Error: Not your turn");
                return;
            }

            try {
                game.makeMove(move);
            } catch (chess.InvalidMoveException e) {
                sendError(session1, "Error: " + e.getMessage());
                return;
            }

            updateGameInDatabase(gameData, game);

            LoadGameMessage loadGameMessage = new LoadGameMessage(game);
            for (Connection connection : connections.values()) {
                if (connection.gameID.equals(gameID)) {
                    sendMessage(connection.session, loadGameMessage);
                }
            }

            sendNotificationToOthers(authToken, gameID, authData.username() + " made a move");
        });
    }

    private void leave(Session session, UserGameCommand command) throws IOException {
        // TODO: Implement leave logic
        sendError(session, "Leave not implemented yet");
    }

    private void resign(Session session, UserGameCommand command) throws IOException {
        executeWithValidation(session, command, (session1, authToken, gameID, authData, gameData) -> {
            String playerUsername = authData.username();
            if (!playerUsername.equals(gameData.whiteUsername()) &&
                    !playerUsername.equals(gameData.blackUsername())) {
                sendError(session1, "Error: Only players can resign");
                return;
            }

            ChessGame game = getGameWithFallback(gameData);
            
            if (game.isGameOver()) {
                sendError(session1, "Error: Game is already over");
                return;
            }

            game.setGameOver(true);

            updateGameInDatabase(gameData, game);

            NotificationMessage notification = new NotificationMessage(playerUsername + " resigned the game");
            for (Connection connection : connections.values()) {
                if (connection.gameID.equals(gameID)) {
                    sendMessage(connection.session, notification);
                }
            }
        });
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