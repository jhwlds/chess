package service;

import dataaccess.*;
import model.*;
import shared.RegisterRequest;
import shared.RegisterResult;
import java.util.UUID;

public class UserService {

    private final UserDAO userDAO       = DAOFactory.userDAO();
    private final AuthTokenDAO tokenDAO = DAOFactory.authDAO();

    public RegisterResult register(RegisterRequest req) {
        try {
            if (req.username() == null || req.password() == null || req.email() == null) {
                return new RegisterResult("Error: bad request");
            }
            if (userDAO.getUser(req.username()) != null) {
                return new RegisterResult("Error: already taken");
            }

            userDAO.insertUser(new UserData(req.username(), req.password(), req.email()));
            String token = UUID.randomUUID().toString();
            tokenDAO.insertAuth(new AuthData(token, req.username()));

            return new RegisterResult(req.username(), token);

        } catch (DataAccessException e) {
            return new RegisterResult("Error: " + e.getMessage());
        }
    }
}
