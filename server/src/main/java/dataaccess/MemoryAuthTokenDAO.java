package dataaccess;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthTokenDAO implements AuthTokenDAO {
    private final Map<String, String> tokens = new HashMap<>();

    @Override
    public void clear() {
        tokens.clear();
    }
}
