package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DAOFactory;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import shared.LoginRequest;
import shared.LoginResult;
import org.mindrot.jbcrypt.BCrypt;

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
            if (user == null || !BCrypt.checkpw(req.password(), user.password())) {
                return new LoginResult("Error: unauthorized");
            }

            String token = UUID.randomUUID().toString();
            authDAO.insertAuth(new AuthData(token, req.username()));
            return new LoginResult(req.username(), token, null);

        } catch (DataAccessException e) {
            return new LoginResult("Error: internal");
        }
    }
}
