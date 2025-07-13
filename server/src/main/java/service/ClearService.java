package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import shared.ClearResult;

public class ClearService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthTokenDAO tokenDAO;

    public ClearService(UserDAO userDAO, GameDAO gameDAO, AuthTokenDAO tokenDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.tokenDAO = tokenDAO;
    }

    public ClearResult clearAll() throws DataAccessException {
        userDAO.clear();
        gameDAO.clear();
        tokenDAO.clear();
        return new ClearResult("Clear succeeded");
    }
}
