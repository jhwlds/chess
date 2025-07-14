package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DAOFactory;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import shared.LoginRequest;
import shared.LoginResult;

import java.util.UUID;

public class LoginService {
    private final UserDAO userDAO = DAOFactory.userDAO();
    private final AuthTokenDAO authDAO = DAOFactory.authDAO();

    public LoginResult login(LoginRequest req) {
        try {
            if (req.username() == null || req.password() == null) {
                return new LoginResult("Error: bad request");
            }

            UserData user = userDAO.getUser(req.username());
            if (user == null || !user.password().equals(req.password())) {
                return new LoginResult("Error: unauthorized");
            }

            String token = UUID.randomUUID().toString();
            authDAO.insertAuth(new AuthData(token, req.username()));
            return new LoginResult(req.username(), token, null);

        } catch (DataAccessException e) {
            return new LoginResult("Error: " + e.getMessage());
        }
    }
}
