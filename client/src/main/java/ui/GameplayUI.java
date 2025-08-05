package ui;

import chess.ChessGame;
import client.WebSocketClient;

import java.util.Scanner;

public class GameplayUI {
    private final Scanner scanner;
    private final String playerColor;
    private final WebSocketClient webSocketClient;
    private ChessGame currentGame;

    public GameplayUI(String playerColor, WebSocketClient webSocketClient) {
        this.scanner = new Scanner(System.in);
        this.playerColor = playerColor;
        this.webSocketClient = webSocketClient;
        this.currentGame = new ChessGame();
    }

    public void run() {
        System.out.println("Entering gameplay mode...");
        System.out.println("Type 'help' for available commands");

        while (true) {
            System.out.print("\n[GAMEPLAY] >>> ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "help":
                    showHelp();
                    break;
                case "redraw":
                    redrawBoard();
                    break;
                case "leave":
                    handleLeave();
                    return;
                case "move":
                    handleMakeMove();
                    break;
                case "resign":
                    handleResign();
                    break;
                case "highlight":
                    handleHighlightLegalMoves();
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
            }
        }
    }

    public void updateGame(ChessGame game) {
    }

    private void showHelp() {
    }

    private void redrawBoard() {
    }

    private void handleLeave() {
    }

    private void handleMakeMove() {
    }

    private void handleResign() {
    }

    private void handleHighlightLegalMoves() {
    }

} 