package client;

import model.AuthData;
import shared.CreateGameResult;
import shared.GameListResult;
import org.junit.jupiter.api.*;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void setUp() throws Exception {
        // Clear database before each test
        facade.clearDatabase();
    }

    @Test
    void registerPositive() throws Exception {
        AuthData authData = facade.register("player1", "password", "p1@email.com");
        assertNotNull(authData);
        assertTrue(authData.authToken().length() > 10);
        assertEquals("player1", authData.username());
    }

    @Test
    void registerNegative() {
        assertThrows(Exception.class, () -> {
            // Try to register with null username
            facade.register(null, "password", "email@test.com");
        });
    }

    @Test
    void loginPositive() throws Exception {
        // First register a user
        facade.register("player1", "password", "p1@email.com");

        // Then login
        AuthData authData = facade.login("player1", "password");
        assertNotNull(authData);
        assertTrue(authData.authToken().length() > 10);
        assertEquals("player1", authData.username());
    }

    @Test
    void loginNegative() {
        assertThrows(Exception.class, () -> {
            // Try to login with wrong password
            facade.login("nonexistent", "wrongpassword");
        });
    }

    @Test
    void logoutPositive() throws Exception {
        // First register and get auth token
        AuthData authData = facade.register("player1", "password", "p1@email.com");

        // Then logout
        assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    void logoutNegative() {
        assertThrows(Exception.class, () -> {
            // Try to logout with invalid token
            facade.logout("invalid-token");
        });
    }

    @Test
    void createGamePositive() throws Exception {
        // First register and get auth token
        AuthData authData = facade.register("player1", "password", "p1@email.com");

        // Then create a game
        CreateGameResult response = facade.createGame("Test Game", authData.authToken());
        assertNotNull(response);
        assertNotNull(response.gameID());
        assertTrue(response.gameID() > 0);
    }

    @Test
    void createGameNegative() {
        assertThrows(Exception.class, () -> {
            // Try to create game without auth token
            facade.createGame("Test Game", "invalid-token");
        });
    }

    @Test
    void listGamesPositive() throws Exception {
        // First register and get auth token
        AuthData authData = facade.register("player1", "password", "p1@email.com");

        // Create a game
        facade.createGame("Test Game", authData.authToken());

        // Then list games
        GameListResult response = facade.listGames(authData.authToken());
        assertNotNull(response);
        assertNotNull(response.games());
        assertEquals(1, response.games().size());
        assertEquals("Test Game", response.games().get(0).gameName());
    }

    @Test
    void listGamesNegative() {
        assertThrows(Exception.class, () -> {
            // Try to list games without auth token
            facade.listGames("invalid-token");
        });
    }

    @Test
    void joinGamePositive() throws Exception {
        // First register and get auth token
        AuthData authData = facade.register("player1", "password", "p1@email.com");

        // Create a game
        CreateGameResult createResponse = facade.createGame("Test Game", authData.authToken());

        // Then join the game
        assertDoesNotThrow(() ->
                facade.joinGame("WHITE", createResponse.gameID(), authData.authToken())
        );
    }

    @Test
    void joinGameNegative() {
        assertThrows(Exception.class, () -> {
            // Try to join non-existent game
            facade.joinGame("WHITE", 99999, "invalid-token");
        });
    }

    @Test
    void clearDatabasePositive() throws Exception {
        // Should not throw exception
        assertDoesNotThrow(() -> facade.clearDatabase());
    }
}
