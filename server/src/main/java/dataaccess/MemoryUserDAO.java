package dataaccess;

import java.util.HashMap;
import java.util.Map;
import model.UserData;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
    }

    // 나중에 createUser, getUser 등 추가
}

