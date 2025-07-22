package passoff.server.dataaccess;

import chess.ChessGame;
import dataaccess.*;
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
    @DisplayName("01-A insertGame Success")
    void insertGamePositive() throws DataAccessException {
        var game = new GameData(0, "testGame", null, null, new ChessGame());
        int id = dao.insertGame(game);
        assertTrue(id > 0);
    }

    @Test
    @DisplayName("01-B insertGame Failure - Invalid JSON")
    void insertGameNegative() {
        assertThrows(DataAccessException.class, () -> {
            // game 객체를 null로 넣어 JSON 변환 실패 유도
            var invalidGame = new GameData(0, "bad", null, null, null);
            dao.insertGame(invalidGame);
        });
    }

    @Test
    @DisplayName("02-A getGame Success")
    void getGamePositive() throws DataAccessException {
        var game = new GameData(0, "game2", "white", "black", new ChessGame());
        int id = dao.insertGame(game);
        var result = dao.getGame(id);
        assertEquals("game2", result.gameName());
    }

    @Test
    @DisplayName("02-B getGame Failure - Not Found")
    void getGameNegative() throws DataAccessException {
        var result = dao.getGame(-999);
        assertNull(result);
    }

    @Test
    @DisplayName("03-A updateGame Success")
    void updateGamePositive() throws DataAccessException {
        var game = new GameData(0, "beforeUpdate", "w", "b", new ChessGame());
        int id = dao.insertGame(game);

        var updated = new GameData(id, "afterUpdate", "alice", "bob", new ChessGame());
        dao.updateGame(updated);

        var result = dao.getGame(id);
        assertEquals("afterUpdate", result.gameName());
        assertEquals("alice", result.whiteUsername());
        assertEquals("bob", result.blackUsername());
    }

    @Test
    @DisplayName("03-B updateGame Failure - Nonexistent ID")
    void updateGameNegative() {
        assertThrows(DataAccessException.class, () -> {
            var badUpdate = new GameData(-1, "bad", null, null, new ChessGame());
            dao.updateGame(badUpdate);
        });
    }

    @Test
    @DisplayName("04-A listGames Success")
    void listGamesPositive() throws DataAccessException {
        dao.insertGame(new GameData(0, "g1", null, null, new ChessGame()));
        dao.insertGame(new GameData(0, "g2", null, null, new ChessGame()));
        List<GameData> games = dao.listGames();
        assertEquals(2, games.size());
    }

    @Test
    @DisplayName("04-B listGames Empty")
    void listGamesNegative() throws DataAccessException {
        List<GameData> games = dao.listGames();
        assertEquals(0, games.size());
    }

    @Test
    @DisplayName("05-A clear Success")
    void clearPositive() throws DataAccessException {
        dao.insertGame(new GameData(0, "g", null, null, new ChessGame()));
        dao.clear();
        assertEquals(0, dao.listGames().size());
    }
}
