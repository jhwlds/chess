package handler;

import com.google.gson.Gson;
import service.GameService;
import shared.GameListResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {
    private static final Gson GSON = new Gson();
    private static final GameService SERVICE = new GameService();

    @Override
    public Object handle(Request req, Response res) {
        String authToken = req.headers("Authorization");
        GameListResult result = SERVICE.listGames(authToken);

        ResponseUtils.applyStatus(res, result.message());
        res.type("application/json");
        return GSON.toJson(result);
    }
}