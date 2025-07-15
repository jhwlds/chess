package handler;

import com.google.gson.Gson;
import service.LoginService;
import shared.LoginRequest;
import shared.LoginResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    private static final Gson GSON = new Gson();
    private static final LoginService SERVICE = new LoginService();

    @Override
    public Object handle(Request req, Response res) {
        LoginRequest request = GSON.fromJson(req.body(), LoginRequest.class);
        LoginResult result = SERVICE.login(request);

        ResponseUtils.applyStatus(res, result.message());
        res.type("application/json");
        return GSON.toJson(result);
    }
}
