package websocket;

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


        } catch (DataAccessException e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void makeMove(Session session, UserGameCommand command) throws IOException {
        // TODO: Implement make move logic
        sendError(session, "Make move not implemented yet");
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

    }

    private void sendError(Session session, String errorMessage) throws IOException {

    }

    private void sendNotificationToOthers(String excludeAuthToken, Integer gameID, String message) throws IOException {

    }
} 