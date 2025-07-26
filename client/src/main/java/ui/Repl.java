package ui;

import chess.ChessGame;
import client.ServerFacade;
import model.AuthData;
import shared.GameListResult;

import java.util.Scanner;

public class Repl {
    private final ServerFacade serverFacade;
    private final Scanner scanner;
    private String authToken;
    private String username;
    private String playerColor;

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
        System.out.println("  play <gameID> <WHITE|BLACK> - Join a game as a player");
        System.out.println("  observe <gameID> <WHITE|BLACK> - Observe a game from chosen perspective");
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
            System.out.println("Login failed: " + e.getMessage());
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
            System.out.println("Registration failed: " + e.getMessage());
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
            System.out.println("Logout failed: " + e.getMessage());
        }
    }

    private void handleCreateGame() {
        try {
            System.out.print("Game name: ");
            String gameName = scanner.nextLine().trim();

            var response = serverFacade.createGame(gameName, authToken);
            if (response.gameID() != null) {
                System.out.println("Game created successfully with ID: " + response.gameID());
            } else {
                System.out.println("Failed to create game: " + response.message());
            }
        } catch (Exception e) {
            System.out.println("Create game failed: " + e.getMessage());
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
                    for (var game : response.games()) {
                        System.out.printf("  %d. %s (White: %s, Black: %s)%n",
                                game.gameID(), game.gameName(),
                                game.whiteUsername() != null ? game.whiteUsername() : "none",
                                game.blackUsername() != null ? game.blackUsername() : "none");
                    }
                }
            } else {
                System.out.println("Failed to list games: " + response.message());
            }
        } catch (Exception e) {
            System.out.println("List games failed: " + e.getMessage());
        }
    }

    private void handlePlayGame() {
        try {
            System.out.print("Game ID: ");
            int gameID = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Color (WHITE/BLACK): ");
            String color = scanner.nextLine().trim().toUpperCase();

            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                System.out.println("Invalid color. Please choose WHITE or BLACK.");
                return;
            }

            serverFacade.joinGame(color, gameID, authToken);
            this.playerColor = color;
            System.out.println("Successfully joined game " + gameID + " as " + color);

            drawChessBoard();
        } catch (NumberFormatException e) {
            System.out.println("Invalid game ID. Please enter a number.");
        } catch (Exception e) {
            System.out.println("Join game failed: " + e.getMessage());
        }
    }

    private void handleObserveGame() {
        try {
            System.out.print("Game ID: ");
            int gameID = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Color to observe (WHITE/BLACK): ");
            String color = scanner.nextLine().trim().toUpperCase();

            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                System.out.println("Invalid color. Please choose WHITE or BLACK.");
                return;
            }

            this.playerColor = color;
            System.out.println("Observing game " + gameID + " from " + color + " perspective");
            drawChessBoard();
        } catch (NumberFormatException e) {
            System.out.println("Invalid game ID. Please enter a number.");
        } catch (Exception e) {
            System.out.println("Observe game failed: " + e.getMessage());
        }
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

}
