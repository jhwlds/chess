package handler;

import com.google.gson.Gson;
import service.LogoutService;
import shared.LogoutResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    private final Gson gson = new Gson();

    @Override
    public Object handle(Request req, Response res) {
        String authToken = req.headers("Authorization");
        LogoutService service = new LogoutService();
        LogoutResult result = service.logout(authToken);

        if ("Error: unauthorized".equals(result.message())) {
            res.status(401);
        } else if (result.message() != null) {
            res.status(500);
        } else {
            res.status(200);
        }

        res.type("application/json");
        return gson.toJson(result);
    }
}
