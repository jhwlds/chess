package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import shared.RegisterRequest;
import shared.LoginRequest;
import shared.CreateGameRequest;
import shared.CreateGameResult;
import shared.JoinGameRequest;
import shared.GameListResult;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson = new Gson();

    private final int port;

    public ServerFacade(int port) {
        this.port = port;
        this.serverUrl = "http://localhost:" + port;
    }

    public int getPort() {
        return port;
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws Exception {
        URL url = new URI(serverUrl + path).toURL();
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(method);
        http.setDoOutput(true);

        http.setRequestProperty("Content-Type", "application/json");
        if (authToken != null) {
            http.setRequestProperty("Authorization", authToken);
        }

        if (request != null) {
            String jsonRequest = gson.toJson(request);
            try (OutputStream os = http.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        int statusCode = http.getResponseCode();
        InputStream inputStream = (statusCode >= 200 && statusCode < 300) ? http.getInputStream() : http.getErrorStream();

        if (inputStream == null) {
            throw new Exception("No response received");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            if (statusCode >= 200 && statusCode < 300) {
                if (responseClass == null) {
                    return null;
                }
                return gson.fromJson(response.toString(), responseClass);
            } else {
                throw new Exception("HTTP " + statusCode + ": " + response.toString());
            }
        }
    }

    public AuthData register(String username, String password, String email) throws Exception {
        var request = new RegisterRequest(username, password, email);
        return makeRequest("POST", "/user", request, AuthData.class, null);
    }

    public AuthData login(String username, String password) throws Exception {
        var request = new LoginRequest(username, password);
        return makeRequest("POST", "/session", request, AuthData.class, null);
    }

    public void logout(String authToken) throws Exception {
        makeRequest("DELETE", "/session", null, null, authToken);
    }

    public GameListResult listGames(String authToken) throws Exception {
        return makeRequest("GET", "/game", null, GameListResult.class, authToken);
    }

    public CreateGameResult createGame(String gameName, String authToken) throws Exception {
        var request = new CreateGameRequest(gameName);
        return makeRequest("POST", "/game", request, CreateGameResult.class, authToken);
    }

    public void joinGame(String playerColor, int gameID, String authToken) throws Exception {
        ChessGame.TeamColor color = null;
        if ("WHITE".equalsIgnoreCase(playerColor)) {
            color = ChessGame.TeamColor.WHITE;
        } else if ("BLACK".equalsIgnoreCase(playerColor)) {
            color = ChessGame.TeamColor.BLACK;
        }
        var request = new JoinGameRequest(color, gameID);
        makeRequest("PUT", "/game", request, null, authToken);
    }

    public void clearDatabase() throws Exception {
        makeRequest("DELETE", "/db", null, null, null);
    }
}