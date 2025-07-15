package handler;

import spark.Response;

public class ResponseUtils {

    public static void applyStatus(Response res, String message) {
        if (message == null || !message.startsWith("Error")) {
            res.status(200);
        } else if ("Error: bad request".equals(message)) {
            res.status(400);
        } else if ("Error: unauthorized".equals(message)) {
            res.status(401);
        } else if ("Error: already taken".equals(message)) {
            res.status(403);
        } else {
            res.status(500);
        }
    }
}
