package shared;

public record CreateGameResult(Integer gameID, String message) {
    public CreateGameResult(String message) {
        this(null, message);
    }
}
