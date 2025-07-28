package ui;

import chess.ChessGame;
import client.ServerFacade;
import model.AuthData;
import shared.GameListResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Repl {
    private final ServerFacade serverFacade;
    private final Scanner scanner;
    private String authToken;
    private String username;
    private String playerColor;
    private final Map<Integer, Integer> gameIdToSequential = new HashMap<>();
    private int nextSequentialId = 1;

    public Repl(int port) {
        this.serverFacade = new ServerFacade(port);
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("♕ 240 Chess Client");
        System.out.println("Type 'help' for a list of commands");

        while (true) {
            if (authToken == null) {
                preloginUI();
            } else {
                postloginUI();
            }
        }
    }

    private void preloginUI() {
        System.out.print("\n[LOGGED_OUT] >>> ");
        String input = scanner.nextLine().trim().toLowerCase();

        switch (input) {
            case "help":
                showPreloginHelp();
                break;
            case "quit":
                System.out.println("Goodbye!");
                System.exit(0);
                break;
            case "login":
                handleLogin();
                break;
            case "register":
                handleRegister();
                break;
            default:
                System.out.println("Unknown command. Type 'help' for a list of commands.");
        }
    }

    private void postloginUI() {
        System.out.print("\n[" + username + "] >>> ");
        String input = scanner.nextLine().trim().toLowerCase();

        switch (input) {
            case "help":
                showPostloginHelp();
                break;
            case "logout":
                handleLogout();
                break;
            case "create":
                handleCreateGame();
                break;
            case "list":
                handleListGames();
                break;
            case "play":
                handlePlayGame();
                break;
            case "observe":
                handleObserveGame();
                break;
            default:
                System.out.println("Unknown command. Type 'help' for a list of commands.");
        }
    }

    private void showPreloginHelp() {
        System.out.println("Available commands:");
        System.out.println("  help - Show this help message");
        System.out.println("  quit - Exit the program");
        System.out.println("  login - Log in to your account");
        System.out.println("  register - Create a new account");
    }

    private void showPostloginHelp() {
        System.out.println("Available commands:");
        System.out.println("  help - Show this help message");
        System.out.println("  logout - Log out of your account");
        System.out.println("  create - Create a new game");
        System.out.println("  list - List all games");
        System.out.println("  play <gameNumber> <WHITE|BLACK> - Join a game as a player");
        System.out.println("  observe <gameNumber> <WHITE|BLACK> - Observe a game from chosen perspective");
    }

    private void handleLogin() {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            AuthData authData = serverFacade.login(username, password);
            this.authToken = authData.authToken();
            this.username = authData.username();
            this.playerColor = null;
            System.out.println("Successfully logged in as " + username);
        } catch (Exception e) {
            System.out.println("Login failed: " + getUserFriendlyErrorMessage(e.getMessage()));
        }
    }

    private void handleRegister() {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            AuthData authData = serverFacade.register(username, password, email);
            this.authToken = authData.authToken();
            this.username = authData.username();
            this.playerColor = null;
            System.out.println("Successfully registered and logged in as " + username);
        } catch (Exception e) {
            System.out.println("Registration failed: " + getUserFriendlyErrorMessage(e.getMessage()));
        }
    }

    private void handleLogout() {
        try {
            serverFacade.logout(authToken);
            this.authToken = null;
            this.username = null;
            this.playerColor = null;
            System.out.println("Successfully logged out");
        } catch (Exception e) {
            System.out.println("Logout failed: " + getUserFriendlyErrorMessage(e.getMessage()));
        }
    }

    private void handleCreateGame() {
        try {
            System.out.print("Game name: ");
            String gameName = scanner.nextLine().trim();

            var response = serverFacade.createGame(gameName, authToken);
            if (response.gameID() != null) {
                int sequentialId = nextSequentialId++;
                gameIdToSequential.put(response.gameID(), sequentialId);
                System.out.println("Game " + sequentialId + " created successfully");
            } else {
                System.out.println("Failed to create game: " + getUserFriendlyErrorMessage(response.message()));
            }
        } catch (Exception e) {
            System.out.println("Create game failed: " + getUserFriendlyErrorMessage(e.getMessage()));
        }
    }

    private void handleListGames() {
        try {
            GameListResult response = serverFacade.listGames(authToken);
            if (response.games() != null) {
                System.out.println("Games:");
                if (response.games().isEmpty()) {
                    System.out.println("  No games available");
                } else {
                    gameIdToSequential.clear();
                    nextSequentialId = 1;
                    
                    for (var game : response.games()) {
                        int sequentialId = nextSequentialId++;
                        gameIdToSequential.put(game.gameID(), sequentialId);
                        System.out.printf("  %d. %s (White: %s, Black: %s)%n",
                                sequentialId, game.gameName(),
                                game.whiteUsername() != null ? game.whiteUsername() : "none",
                                game.blackUsername() != null ? game.blackUsername() : "none");
                    }
                }
            } else {
                System.out.println("Failed to list games: " + getUserFriendlyErrorMessage(response.message()));
            }
        } catch (Exception e) {
            System.out.println("List games failed: " + getUserFriendlyErrorMessage(e.getMessage()));
        }
    }

    private Integer findActualGameId(int sequentialId) {
        for (Map.Entry<Integer, Integer> entry : gameIdToSequential.entrySet()) {
            if (entry.getValue() == sequentialId) {
                return entry.getKey();
            }
        }
        return null;
    }

    private GameInputResult getGameInput(String colorPrompt) {
        try {
            System.out.print("Game number: ");
            int sequentialId = Integer.parseInt(scanner.nextLine().trim());
            System.out.print(colorPrompt);
            String color = scanner.nextLine().trim().toUpperCase();

            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                System.out.println("Invalid color. Please choose WHITE or BLACK.");
                return null;
            }

            Integer actualGameId = findActualGameId(sequentialId);

            if (actualGameId == null) {
                System.out.println("Invalid game number. Please enter a valid game number from the list.");
                return null;
            }

            return new GameInputResult(sequentialId, actualGameId, color);
        } catch (NumberFormatException e) {
            System.out.println("Invalid game number. Please enter a number.");
            return null;
        }
    }

    private static class GameInputResult {
        final int sequentialId;
        final int actualGameId;
        final String color;

        GameInputResult(int sequentialId, int actualGameId, String color) {
            this.sequentialId = sequentialId;
            this.actualGameId = actualGameId;
            this.color = color;
        }
    }

    private void handlePlayGame() {
        GameInputResult input = getGameInput("Color (WHITE/BLACK): ");
        if (input == null) {
            return;
        }

        try {
            serverFacade.joinGame(input.color, input.actualGameId, authToken);
            this.playerColor = input.color;
            System.out.println("Successfully joined game " + input.sequentialId + " as " + input.color);

            drawChessBoard();
        } catch (Exception e) {
            System.out.println("Join game failed: " + getUserFriendlyErrorMessage(e.getMessage()));
        }
    }

    private void handleObserveGame() {
        GameInputResult input = getGameInput("Color to observe (WHITE/BLACK): ");
        if (input == null) {
            return;
        }

        this.playerColor = input.color;
        System.out.println("Observing game " + input.sequentialId + " from " + input.color + " perspective");
        drawChessBoard();
    }

    private void drawChessBoard() {

        ChessGame game = new ChessGame();

        if (playerColor.equals("WHITE")) {
            System.out.println("\n=== YOUR BOARD (WHITE PERSPECTIVE) ===");
            ChessBoardDrawer.drawBoard(game, true);
        } else if (playerColor.equals("BLACK")) {
            System.out.println("\n=== YOUR BOARD (BLACK PERSPECTIVE) ===");
            ChessBoardDrawer.drawBoard(game, false);
        }

        System.out.println("\nNote: This is a static board. Gameplay will be implemented in Phase 6.");
    }

    private String getUserFriendlyErrorMessage(String message) {
        if (message == null) {
            return "An unknown error occurred.";
        }
        
        if (message.contains("HTTP 400") || message.contains("bad request")) {
            return "Invalid request. Please check your input and try again.";
        }
        if (message.contains("HTTP 401") || message.contains("unauthorized")) {
            return "You are not authorized. Please log in again.";
        }
        if (message.contains("HTTP 403") || message.contains("already taken")) {
            return "This resource is already taken. Please try a different option.";
        }
        if (message.contains("HTTP 500") || message.contains("internal")) {
            return "Server error. Please try again later.";
        }
        if (message.contains("Game name already in use")) {
            return "Game name already in use. Please choose a different name.";
        }
        if (message.contains("Invalid username or password")) {
            return "Invalid username or password. Please try again.";
        }
        if (message.contains("Username already in use")) {
            return "Username already in use. Please choose a different username.";
        }
        if (message.contains("Invalid email")) {
            return "Invalid email address. Please enter a valid email.";
        }
        if (message.contains("No response received")) {
            return "Unable to connect to server. Please check your connection.";
        }
        if (message.contains("HTTP 404")) {
            return "Resource not found. Please check your input.";
        }
        
        // Remove technical details and return a cleaner message
        return message.replaceAll("HTTP \\d+: ", "").replaceAll("Error: ", "");
    }
}
