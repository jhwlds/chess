package handler;

import com.google.gson.Gson;
import service.ClearService;
import shared.ClearResult;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryAuthTokenDAO;
import dataaccess.DataAccessException;

import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {
    private final Gson gson = new Gson();

    @Override
    public Object handle(Request req, Response res) {
        System.out.println("[DEBUG] ClearHandler was called!");

        ClearService service = new ClearService(
                new MemoryUserDAO(),
                new MemoryGameDAO(),
                new MemoryAuthTokenDAO()
        );

        try {
            ClearResult result = service.clearAll();
            res.status(200);
            res.type("application/json");
            return gson.toJson(result);
        } catch (DataAccessException e) {
            res.status(500);
            res.type("application/json");
            return gson.toJson(new ClearResult("Error: " + e.getMessage()));
        }
    }
}
