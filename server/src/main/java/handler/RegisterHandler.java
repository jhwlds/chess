package handler;

import com.google.gson.Gson;
import service.UserService;
import shared.RegisterRequest;
import shared.RegisterResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    private static final Gson GSON = new Gson();
    private static final UserService SERVICE = new UserService();

    @Override
    public Object handle(Request req, Response res) {
        RegisterRequest request = GSON.fromJson(req.body(), RegisterRequest.class);
        RegisterResult result = SERVICE.register(request);

        ResponseUtils.applyStatus(res, result.message());
        res.type("application/json");
        return GSON.toJson(result);
    }
}
