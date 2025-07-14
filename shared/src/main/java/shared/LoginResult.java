package shared;

public record LoginResult(String username, String authToken, String message) {
    public LoginResult(String message) {
        this(null, null, message);
    }
}

