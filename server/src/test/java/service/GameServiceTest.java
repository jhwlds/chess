package service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import shared.*;
import dataaccess.DataAccessException;
import chess.ChessGame;

public class GameServiceTest {

    @Test
    public void testCreateGameSuccess() throws DataAccessException {
        UserService userService = new UserService();
        RegisterResult reg = userService.register(new RegisterRequest("gameUser", "pw", "m@m.com"));
        GameService service = new GameService();
        CreateGameResult result = service.createGame(reg.authToken(), new CreateGameRequest("Cool Game"));
        assertNotNull(result.gameID());
    }

    @Test
    public void testCreateGameUnauthorized() {
        GameService service = new GameService();
        CreateGameResult result = service.createGame("invalid", new CreateGameRequest("Cool Game"));
        assertEquals("Error: unauthorized", result.message());
    }

    @Test
    public void testJoinGameSuccess() throws DataAccessException {
        UserService userService = new UserService();
        RegisterResult reg = userService.register(new RegisterRequest("joinUser", "pw", "m@m.com"));
        GameService service = new GameService();
        CreateGameResult created = service.createGame(reg.authToken(), new CreateGameRequest("Game"));
        JoinGameRequest joinReq = new JoinGameRequest(ChessGame.TeamColor.WHITE, created.gameID());
        JoinGameResult result = service.joinGame(reg.authToken(), joinReq);
        assertNull(result.message());
    }

    @Test
    public void testJoinGameSpotTaken() throws DataAccessException {
        UserService userService = new UserService();
        RegisterResult reg = userService.register(new RegisterRequest("userA", "pw", "a@a.com"));
        RegisterResult regB = userService.register(new RegisterRequest("userB", "pw", "b@b.com"));
        GameService service = new GameService();
        CreateGameResult created = service.createGame(reg.authToken(), new CreateGameRequest("Game"));
        JoinGameRequest first = new JoinGameRequest(ChessGame.TeamColor.WHITE, created.gameID());
        service.joinGame(reg.authToken(), first);
        JoinGameResult second = service.joinGame(regB.authToken(), first);
        assertEquals("Error: already taken", second.message());
    }

    @Test
    public void testListGamesUnauthorized() {
        GameService service = new GameService();
        GameListResult result = service.listGames("invalid");
        assertEquals("Error: unauthorized", result.message());
    }
}
