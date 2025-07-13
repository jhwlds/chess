package dataaccess;

import model.AuthData;

public interface AuthTokenDAO {
    void insertAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String token) throws DataAccessException;
    void deleteAuth(String token) throws DataAccessException;
    void clear() throws DataAccessException;
}
