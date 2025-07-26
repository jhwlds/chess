package client;

import com.google.gson.Gson;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
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
                throw new Exception("HTTP " + statusCode + ": " + response);
            }
        }
    }
}