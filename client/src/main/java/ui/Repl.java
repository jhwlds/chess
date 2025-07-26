package ui;

import client.ServerFacade;
import model.AuthData;
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

    private void postloginUI() {}

    private void showPreloginHelp() {
        System.out.println("Available commands:");
        System.out.println("  help - Show this help message");
        System.out.println("  quit - Exit the program");
        System.out.println("  login - Log in to your account");
        System.out.println("  register - Create a new account");
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

}
