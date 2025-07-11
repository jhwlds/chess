package handler;

import com.google.gson.Gson;
import service.ClearService;
import shared.ClearResult;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryAuthTokenDAO;

import spark.Request;
import spark.Response;

public class ClearHandler {
    public Object handle(Request req, Response res) {
        System.out.println("[DEBUG] ClearHandler was called!");
        var service = new ClearService(
                new MemoryUserDAO(),
                new MemoryGameDAO(),
                new MemoryAuthTokenDAO()
        );

        ClearResult result = service.clearAll();
        res.type("application/json");
        return new Gson().toJson(result);
    }
}

