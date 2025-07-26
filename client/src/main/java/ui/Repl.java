package ui;

import client.ServerFacade;
import java.util.Scanner;

public class Repl {
    private final ServerFacade serverFacade;
    private final Scanner scanner;
    private String authToken;

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
        }
    }

    private void postloginUI() {}

    private void showPreloginHelp() {}
}
