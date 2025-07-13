package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthTokenDAO implements AuthTokenDAO {
    private final Map<String, AuthData> authTokens = new HashMap<>();

    @Override
    public void insertAuth(AuthData auth) throws DataAccessException {
        authTokens.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        return authTokens.get(token);
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        authTokens.remove(token);
    }

    @Override
    public void clear() {
        authTokens.clear();
    }
}
