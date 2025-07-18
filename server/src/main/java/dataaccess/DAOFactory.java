package dataaccess;

public final class DAOFactory {

    private static final UserDAO USER_DAO = new MySQLUserDAO();
    private static final AuthTokenDAO AUTH_DAO = new MySQLAuthTokenDAO();
    private static final GameDAO GAME_DAO = new MemoryGameDAO();

    public static UserDAO userDAO()      { return USER_DAO; }
    public static AuthTokenDAO authDAO() { return AUTH_DAO; }
    public static GameDAO gameDAO()      { return GAME_DAO; }

    private DAOFactory() {}
}
