package handler;

import com.google.gson.Gson;
import service.LoginService;
import shared.LoginRequest;
import shared.LoginResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    private final Gson GSON = new Gson();

    @Override
    public Object handle(Request req, Response res) {
        LoginRequest request = GSON.fromJson(req.body(), LoginRequest.class);
        LoginService service = new LoginService();
        LoginResult result = service.login(request);

        if ("Error: bad request".equals(result.message())) {
            res.status(400);
        } else if ("Error: unauthorized".equals(result.message())) {
            res.status(401);
        } else if (result.message() != null) {
            res.status(500);
        } else {
            res.status(200);
        }

        res.type("application/json");
        return GSON.toJson(result);
    }
}
