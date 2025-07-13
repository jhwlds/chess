package handler;

import com.google.gson.Gson;
import service.ClearService;
import shared.ClearResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {

    private static final Gson gson = new Gson();
    private static final ClearService service = new ClearService();

    @Override
    public Object handle(Request req, Response res) {
        System.out.println("[DEBUG] ClearHandler was called!");

        ClearResult result = service.clearAll();

        if (result.message().startsWith("Error")) {
            res.status(500);
        } else {
            res.status(200);
        }
        res.type("application/json");
        return gson.toJson(result);
    }
}
