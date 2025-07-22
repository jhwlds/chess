package passoff.server.dataaccess;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MySQLUserDAO;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class MySQLUserDAOTest {

    private static UserDAO dao;

    @BeforeAll
    static void setup() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.configureDatabase();
        dao = new MySQLUserDAO();
    }

    @AfterEach
    void clear() throws DataAccessException {
        dao.clear();
    }

    @Test
    @DisplayName("01-A insertUser Success")
    void insertUserPositive() throws DataAccessException {
        var user = new UserData("user1", "pass", "u@e.com");
        dao.insertUser(user);
        var retrieved = dao.getUser("user1");
        assertEquals("user1", retrieved.username());
    }

    @Test
    @DisplayName("01-B insertUser Failure - Duplicate")
    void insertUserNegative() throws DataAccessException {
        var user = new UserData("user1", "pass", "u@e.com");
        dao.insertUser(user);
        assertThrows(DataAccessException.class, () -> dao.insertUser(user));
    }

    @Test
    @DisplayName("02-A getUser Success")
    void getUserPositive() throws DataAccessException {
        var user = new UserData("user2", "pass", "e@x.com");
        dao.insertUser(user);
        assertNotNull(dao.getUser("user2"));
    }

    @Test
    @DisplayName("02-B getUser Failure - Not Found")
    void getUserNegative() throws DataAccessException {
        assertNull(dao.getUser("ghost"));
    }

    @Test
    @DisplayName("03-A Password Match Success")
    void passwordMatchPositive() throws DataAccessException {
        var user = new UserData("user3", "secret", "e@x.com");
        dao.insertUser(user);

        var fromDB = dao.getUser("user3");
        assertTrue(BCrypt.checkpw("secret", fromDB.password()),
                "BCrypt password comparison failed.");
    }

    @Test
    @DisplayName("03-B Password Match Failure - Incorrect Password")
    void passwordMatchNegative() throws DataAccessException {
        var user = new UserData("user4", "pw123", "x@x.com");

        var hashed = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        dao.insertUser(new UserData(user.username(), hashed, user.email()));

        var fromDB = dao.getUser("user4");
        assertFalse(BCrypt.checkpw("wrongpass", fromDB.password()));
    }

    @Test
    @DisplayName("04-A clear Success")
    void clearPositive() throws DataAccessException {
        dao.insertUser(new UserData("u", "p", "e"));
        dao.clear();
        assertNull(dao.getUser("u"));
    }
}
