package shared;

public class RegisterResult {
    public String username;
    public String authToken;
    public String message;

    public RegisterResult(String username, String authToken, String message) {
        this.username = username;
        this.authToken = authToken;
        this.message = message;
    }
}
