package service;

import dataaccess.*;
import shared.ClearResult;

public class ClearService {

    public ClearResult clearAll() {
        try {
            DAOFactory.authDAO().clear();
            DAOFactory.userDAO().clear();
            DAOFactory.gameDAO().clear();
            return new ClearResult("Clear succeeded");
        } catch (DataAccessException e) {
            return new ClearResult("Error: " + e.getMessage());
        }
    }
}
