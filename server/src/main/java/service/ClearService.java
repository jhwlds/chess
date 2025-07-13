package service;

import dataaccess.*;
import shared.ClearResult;

public class ClearService {

    public ClearResult clearAll() {
        try {
            DAOFactory.userDAO().clear();
            DAOFactory.gameDAO().clear();
            DAOFactory.authDAO().clear();
            return new ClearResult("Clear succeeded");
        } catch (DataAccessException e) {
            return new ClearResult("Error: " + e.getMessage());
        }
    }
}
