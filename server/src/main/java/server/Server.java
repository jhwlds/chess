package server;

import handler.LoginHandler;
import spark.*;
import handler.ClearHandler;
import handler.RegisterHandler;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", new ClearHandler());
        Spark.post("/user", new RegisterHandler());
        Spark.post("/session", new LoginHandler());

        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
