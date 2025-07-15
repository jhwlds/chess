package handler;

import com.google.gson.Gson;
import service.LogoutService;
import shared.LogoutResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    private static final Gson GSON = new Gson();
    private static final LogoutService SERVICE = new LogoutService();

    @Override
    public Object handle(Request req, Response res) {
        String authToken = req.headers("Authorization");
        LogoutResult result = SERVICE.logout(authToken);

        ResponseUtils.applyStatus(res, result.message());
        res.type("application/json");
        return GSON.toJson(result);
    }
}
