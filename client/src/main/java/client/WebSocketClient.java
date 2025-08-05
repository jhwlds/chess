package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ui.GameplayUI;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class WebSocketClient {
    private Session session;
    private final String serverUrl;
    private final String authToken;
    private final int gameID;
    private GameplayUI gameplayUI;
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    public WebSocketClient(String serverUrl, String authToken, int gameID, GameplayUI gameplayUI) {
        this.serverUrl = serverUrl;
        this.authToken = authToken;
        this.gameID = gameID;
        this.gameplayUI = gameplayUI;
    }

    public void setGameplayUI(GameplayUI gameplayUI) {
        this.gameplayUI = gameplayUI;
    }

    public void connect() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        String uri = serverUrl.replace("http", "ws") + "/ws";
        container.connectToServer(this, new URI(uri));
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        System.out.println("Connected to server");
        
        // Send CONNECT command
        UserGameCommand connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        sendMessage(connectCommand);
    }

    @OnMessage
    public void onMessage(String message) {
        try {
            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
            
            switch (serverMessage.getServerMessageType()) {
                case LOAD_GAME:
                    LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                    //call the ui
                    break;
                case ERROR:
                    ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
                    System.out.println("Error: " + errorMessage.getErrorMessage());
                    break;
                case NOTIFICATION:
                    NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
                    System.out.println("Notification: " + notificationMessage.getMessage());
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error parsing message: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Connection closed: " + closeReason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("WebSocket error: " + throwable.getMessage());
    }

    public void makeMove(ChessMove move) {
        UserGameCommand moveCommand = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
        moveCommand.setMove(move);
        sendMessage(moveCommand);
    }

    public void resign() {
        UserGameCommand resignCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        sendMessage(resignCommand);
    }

    public void leave() {
        UserGameCommand leaveCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        sendMessage(leaveCommand);
    }

    private void sendMessage(UserGameCommand command) {
        try {
            String message = gson.toJson(command);
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
} 