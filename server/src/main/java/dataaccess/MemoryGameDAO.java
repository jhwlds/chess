package dataaccess;

import java.util.HashMap;
import java.util.Map;
import model.GameData;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        games.clear();
    }
}
