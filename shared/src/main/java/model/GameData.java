package model;

import chess.ChessGame;

public record GameData(
        int gameID,
        String gameName,
        String whiteUsername,
        String blackUsername,
        ChessGame game
) {
    public GameData withWhiteUsername(String whiteUsername) {
        return new GameData(gameID, gameName, whiteUsername, blackUsername, game);
    }

    public GameData withBlackUsername(String blackUsername) {
        return new GameData(gameID, gameName, whiteUsername, blackUsername, game);
    }
}