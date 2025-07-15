package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private final AtomicInteger nextID = new AtomicInteger(1);

    @Override
    public int insertGame(GameData game) {
        int id = nextID.getAndIncrement();
        GameData newGame = new GameData(id, game.gameName(), game.whiteUsername(), game.blackUsername(), game.game());
        games.put(id, newGame);
        return id;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public void clear() {
        games.clear();
        nextID.set(1);
    }
}
