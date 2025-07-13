package dataaccess;

public final class DAOFactory {

    private static final UserDAO userDAO       = new MemoryUserDAO();
    private static final AuthTokenDAO authDAO  = new MemoryAuthTokenDAO();
    private static final GameDAO gameDAO       = new MemoryGameDAO();

    public static UserDAO userDAO()      { return userDAO; }
    public static AuthTokenDAO authDAO() { return authDAO; }
    public static GameDAO gameDAO()      { return gameDAO; }

    private DAOFactory() {}
}
