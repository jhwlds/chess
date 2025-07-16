package shared;

import java.util.List;

public record GameListResult(List<GameInfo> games, String message) {

    public record GameInfo(
            int gameID,
            String whiteUsername,
            String blackUsername,
            String gameName
    ) {}
}
