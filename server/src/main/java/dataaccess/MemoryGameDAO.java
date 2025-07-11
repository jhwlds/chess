package dataaccess;

import java.util.HashMap;
import java.util.Map;
import model.Game;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, Game> games = new HashMap<>();

    @Override
    public void clear() {
        games.clear();
    }
}
