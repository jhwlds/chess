package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DAOFactory;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import shared.LoginRequest;
import shared.LoginResult;

public class LoginService {
    private final UserDAO userDAO = DAOFactory.userDAO();
    private final AuthTokenDAO authDAO = DAOFactory.authDAO();

    public LoginResult login(LoginRequest req) {
        try {
            if (req.username() == null || req.password() == null) {
                return new LoginResult("Error: bad request");
            }

        } catch (DataAccessException e) {
            return new LoginResult("Error: " + e.getMessage());
        }
    }
}
