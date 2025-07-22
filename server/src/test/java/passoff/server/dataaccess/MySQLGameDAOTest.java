package passoff.server.dataaccess;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import dataaccess.MySQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class MySQLGameDAOTest {

    private static GameDAO dao;

    @BeforeAll
    static void setupDB() throws DataAccessException {
        // DB 초기화 - 테이블이 없으면 생성
        DatabaseManager.createDatabase();
        DatabaseManager.configureDatabase();
        dao = new MySQLGameDAO();
    }

    @AfterEach
    void cleanup() throws DataAccessException {
        dao.clear();          // 각 테스트가 고립되도록 보장
    }

    /** 기본 GameData 객체 생성 유틸 */
    private GameData newGame(String name) {
        ChessGame game = new ChessGame();      // 기본 체스 초기 세팅
        return new GameData(-1, name, "white", "black", game);
    }

    /* 1-A  insertGame - Positive */
    @Test
    @DisplayName("01-A insertGame 성공")
    void insertGamePositive() throws DataAccessException {
        int id = dao.insertGame(newGame("match-1"));
        assertTrue(id > 0);

        GameData stored = dao.getGame(id);
        assertEquals("match-1", stored.gameName());
    }

    /* 1-B  insertGame - Negative (null 입력) */
    @Test
    @DisplayName("01-B insertGame 실패-null")
    void insertGameNegative() {
        assertThrows(DataAccessException.class,
                () -> {
                    // insertGame이 null에 대해 예외 던지는 경우에만 유효
                    dao.insertGame(null);
                });
    }
    /* 2-A  getGame - Positive */
    @Test
    @DisplayName("02-A getGame 성공")
    void getGamePositive() throws DataAccessException {
        int id = dao.insertGame(newGame("match-2"));
        assertNotNull(dao.getGame(id));
    }

    /* 2-B  getGame - Negative (존재하지 않는 ID) */
    @Test
    @DisplayName("02-B getGame 실패-미존재")
    void getGameNegative() throws DataAccessException {
        assertNull(dao.getGame(999_999));
    }

    /* 3-A  updateGame - Positive */
    @Test
    @DisplayName("03-A updateGame 성공")
    void updateGamePositive() throws DataAccessException {
        int id = dao.insertGame(newGame("before"));
        GameData changed = new GameData(id, "after", "white", "black",
                dao.getGame(id).game());
        dao.updateGame(changed);

        assertEquals("after", dao.getGame(id).gameName());
    }

    /* 3-B  updateGame - Negative (존재하지 않는 ID) */
    @Test
    @DisplayName("03-B updateGame 실패-미존재")
    void updateGameNegative() {
        GameData phantom = new GameData(42, "ghost", null, null, null);
        assertThrows(DataAccessException.class,
                () -> dao.updateGame(phantom));
    }

    /* 4-A  listGames - Positive */
    @Test
    @DisplayName("04-A listGames 성공")
    void listGamesPositive() throws DataAccessException {
        dao.insertGame(newGame("g1"));
        dao.insertGame(newGame("g2"));
        List<GameData> list = dao.listGames();
        assertEquals(2, list.size());
    }

    /* 4-B  listGames - Negative (비어있을 때) */
    @Test
    @DisplayName("04-B listGames 실패-빈목록")
    void listGamesNegative() throws DataAccessException {
        assertTrue(dao.listGames().isEmpty());
    }

    /* 5-A  clear - Positive */
    @Test
    @DisplayName("05-A clear 성공")
    void clearPositive() throws DataAccessException {
        dao.insertGame(newGame("any"));
        dao.clear();
        assertTrue(dao.listGames().isEmpty());
    }

    /* 6-A  직렬화/역직렬화 무결성 검증 */
    @Test
    @DisplayName("06-A JSON 직렬화 무결성")
    void serializationIntegrity() throws DataAccessException {
        int id = dao.insertGame(newGame("json-check"));
        ChessGame loaded = dao.getGame(id).game();

        // ChessBoard가 ChessGame과 연결되어 있는지 검증
        assertSame(loaded, loaded.getBoard().getGame(),
                "ChessBoard 내부 game 참조가 끊겼습니다.");
    }

}
