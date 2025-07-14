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
    private static final UserService service = new UserService();

    @Override
    public Object handle(Request req, Response res) {
        RegisterRequest request = GSON.fromJson(req.body(), RegisterRequest.class);
        RegisterResult result = service.register(request);

        switch (result.message() == null ? "" : result.message()) {
            case "Error: bad request"   -> res.status(400);
            case "Error: already taken" -> res.status(403);
            case ""                     -> res.status(200);
            default                     -> res.status(500);
        }
        res.type("application/json");
        return GSON.toJson(result);
    }
}
