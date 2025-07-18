package server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import handler.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {

        try {
            DatabaseManager.createDatabase();
            DatabaseManager.configureDatabase();
        } catch (DataAccessException e) {
            System.err.println("Failed to create db:");
            e.printStackTrace();
            System.exit(1);
        }

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", new ClearHandler());
        Spark.post("/user", new RegisterHandler());
        Spark.post("/session", new LoginHandler());
        Spark.delete("/session", new LogoutHandler());
        Spark.post("/game", new CreateGameHandler());
        Spark.put("/game", new JoinGameHandler());
        Spark.get("/game", new ListGamesHandler());

        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
