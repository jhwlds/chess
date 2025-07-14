package shared;

public record LogoutResult(String message) {
    public LogoutResult() {
        this(null);
    }
}
