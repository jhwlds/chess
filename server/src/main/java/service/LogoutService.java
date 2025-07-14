package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DAOFactory;
import dataaccess.DataAccessException;
import shared.LogoutResult;

public class LogoutService {
    private final AuthTokenDAO authTokenDAO = DAOFactory.authDAO();

    public LogoutResult logout(String authToken) {
        try {
            if (authToken == null || authTokenDAO.getAuth(authToken) == null) {
                return new LogoutResult("Error: unauthorized");
            }

            authTokenDAO.deleteAuth(authToken);
            return new LogoutResult();
        } catch (DataAccessException e) {
            return new LogoutResult("Error: " + e.getMessage());
        }
    }
}
