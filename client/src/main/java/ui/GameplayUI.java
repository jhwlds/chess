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
        this.currentGame = game;
        redrawBoard();
    }

    private void showHelp() {
        System.out.println("Available commands:");
        System.out.println("  help - Show this help message");
        System.out.println("  redraw - Redraw the chess board");
        System.out.println("  leave - Leave the game");
        System.out.println("  move - Make a move");
        System.out.println("  resign - Resign the game");
        System.out.println("  highlight - Highlight legal moves for a piece");
    }

    private void redrawBoard() {
        System.out.println("\n=== CHESS BOARD ===");
        boolean whitePerspective = playerColor.equals("WHITE") || playerColor.equals("OBSERVER");
        ChessBoardDrawer.drawBoard(currentGame, whitePerspective);
        System.out.println();
    }

    private void handleLeave() {
        System.out.println("Leaving game...");
        webSocketClient.leave();
        webSocketClient.close();
    }

    private void handleMakeMove() {
    }

    private void handleResign() {
    }

    private void handleHighlightLegalMoves() {
    }
} 