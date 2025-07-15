package handler;

import com.google.gson.Gson;
import service.ClearService;
import shared.ClearResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {

    private static final Gson GSON = new Gson();
    private static final ClearService SERVICE = new ClearService();

    @Override
    public Object handle(Request req, Response res) {
        System.out.println("[DEBUG] ClearHandler was called!");

        ClearResult result = SERVICE.clearAll();

        ResponseUtils.applyStatus(res, result.message());
        res.type("application/json");
        return GSON.toJson(result);
    }
}
