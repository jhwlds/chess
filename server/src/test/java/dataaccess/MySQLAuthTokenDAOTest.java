package dataaccess;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class MySQLAuthTokenDAOTest {

    private static AuthTokenDAO authDAO;
    private static UserDAO userDAO;

    @BeforeAll
    static void setup() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.configureDatabase();
        authDAO = new MySQLAuthTokenDAO();
        userDAO = new MySQLUserDAO();
    }

    @AfterEach
    void clear() throws DataAccessException {
        authDAO.clear();
        userDAO.clear();
    }

    @Test
    @DisplayName("01-A insertAuth Success")
    void insertTokenPositive() throws DataAccessException {
        userDAO.insertUser(new UserData("user1", "password", "e@x.com"));

        var token = new AuthData("token1", "user1");
        authDAO.insertAuth(token);
        assertEquals("user1", authDAO.getAuth("token1").username());
    }

    @Test
    @DisplayName("01-B insertAuth Failure - Duplicate Token")
    void insertTokenNegative() throws DataAccessException {
        userDAO.insertUser(new UserData("user2", "password", "e@x.com"));

        var token = new AuthData("token2", "user2");
        authDAO.insertAuth(token);
        assertThrows(DataAccessException.class, () -> authDAO.insertAuth(token));
    }

    @Test
    @DisplayName("02-A getAuth Success")
    void getAuthPositive() throws DataAccessException {
        userDAO.insertUser(new UserData("user3", "password", "e@x.com"));

        var token = new AuthData("token3", "user3");
        authDAO.insertAuth(token);
        assertEquals("user3", authDAO.getAuth("token3").username());
    }

    @Test
    @DisplayName("02-B getAuth Failure - Token Not Found")
    void getAuthNegative() throws DataAccessException {
        assertNull(authDAO.getAuth("nonexistent-token"));
    }

    @Test
    @DisplayName("03-A clear Success")
    void clearPositive() throws DataAccessException {
        userDAO.insertUser(new UserData("userX", "password", "e@x.com"));

        authDAO.insertAuth(new AuthData("tokenX", "userX"));
        authDAO.clear();
        assertNull(authDAO.getAuth("tokenX"));
    }
}
