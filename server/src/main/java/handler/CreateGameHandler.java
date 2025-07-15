package handler;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.GameService;
import shared.CreateGameRequest;
import shared.CreateGameResult;

public class CreateGameHandler implements Route {
    private static final Gson GSON = new Gson();
    private static final GameService SERVICE = new GameService();

    @Override
    public Object handle(Request req, Response res) {
        String authToken = req.headers("Authorization");
        CreateGameRequest request = GSON.fromJson(req.body(), CreateGameRequest.class);
        CreateGameResult result = SERVICE.createGame(authToken, request);

        ResponseUtils.applyStatus(res, result.message());
        res.type("application/json");
        return GSON.toJson(result);
    }
}
