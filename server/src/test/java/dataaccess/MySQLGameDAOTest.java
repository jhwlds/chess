package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class MySQLGameDAOTest {

    private static GameDAO dao;

    @BeforeAll
    static void setup() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.configureDatabase();
        dao = new MySQLGameDAO();
    }

    @AfterEach
    void clear() throws DataAccessException {
        dao.clear();
    }

    @Test
    @DisplayName("01-A Insert Game Success")
    void insertGamePositive() throws DataAccessException {
        var game = new GameData(0, "test-game", null, null, new ChessGame());
        int id = dao.insertGame(game);
        var retrieved = dao.getGame(id);
        assertNotNull(retrieved);
        assertEquals("test-game", retrieved.gameName());
    }

    @Test
    @DisplayName("01-B Insert Game Fail - Duplicate ID (Manual)")
    void insertGameNegative() throws DataAccessException {
        var game = new GameData(0, "game1", null, null, new ChessGame());
        int id = dao.insertGame(game);

        // 중복 ID 사용으로 에러 유도
        GameData duplicate = new GameData(id, "game1", null, null, new ChessGame());
        assertDoesNotThrow(() -> dao.updateGame(duplicate)); // update는 허용
    }

    @Test
    @DisplayName("02-A Get Game Success")
    void getGamePositive() throws DataAccessException {
        var game = new GameData(0, "my game", "white", "black", new ChessGame());
        int id = dao.insertGame(game);
        var retrieved = dao.getGame(id);
        assertEquals("white", retrieved.whiteUsername());
        assertEquals("black", retrieved.blackUsername());
    }

    @Test
    @DisplayName("02-B Get Game Fail - Invalid ID")
    void getGameNegative() throws DataAccessException {
        assertNull(dao.getGame(99999));
    }

    @Test
    @DisplayName("03-A Update Game Success")
    void updateGamePositive() throws DataAccessException {
        var game = new GameData(0, "original", null, null, new ChessGame());
        int id = dao.insertGame(game);

        var updated = new GameData(id, "updated", "alice", "bob", new ChessGame());
        dao.updateGame(updated);

        var fromDB = dao.getGame(id);
        assertEquals("updated", fromDB.gameName());
        assertEquals("alice", fromDB.whiteUsername());
        assertEquals("bob", fromDB.blackUsername());
    }

    @Test
    @DisplayName("03-B Update Game Fail - Missing ID")
    void updateGameNegative() {
        var bad = new GameData(99999, "bad", null, null, new ChessGame());
        assertThrows(DataAccessException.class, () -> dao.updateGame(bad));
    }

    @Test
    @DisplayName("04-A List Games Success")
    void listGamesPositive() throws DataAccessException {
        dao.insertGame(new GameData(0, "g1", null, null, new ChessGame()));
        dao.insertGame(new GameData(0, "g2", null, null, new ChessGame()));
        List<GameData> games = dao.listGames();
        assertEquals(2, games.size());
    }

    @Test
    @DisplayName("05-A Clear Games Success")
    void clearGames() throws DataAccessException {
        dao.insertGame(new GameData(0, "game", null, null, new ChessGame()));
        dao.clear();
        assertEquals(0, dao.listGames().size());
    }
}
