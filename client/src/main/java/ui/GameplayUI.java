package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPiece;
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
        System.out.print("Enter piece position to highlight legal moves (e.g., 'e2'): ");
        String posStr = scanner.nextLine().trim();

        try {
            ChessPosition position = parsePosition(posStr);
            var legalMoves = currentGame.validMoves(position);

            if (legalMoves != null && !legalMoves.isEmpty()) {
                System.out.println("Legal moves for piece at " + posStr + ":");
                for (ChessMove move : legalMoves) {
                    System.out.println("  " + formatPosition(move.getEndPosition()));
                }
            } else {
                System.out.println("No legal moves for piece at " + posStr);
            }
        } catch (Exception e) {
            System.out.println("Invalid position: " + e.getMessage());
        }
    }

    private ChessPosition parsePosition(String pos) {
        if (pos.length() != 2) {
            throw new IllegalArgumentException("Position must be 2 characters (e.g., 'e2')");
        }

        char colChar = pos.charAt(0);
        char rowChar = pos.charAt(1);

        int col = colChar - 'a' + 1;
        int row = Character.getNumericValue(rowChar);

        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new IllegalArgumentException("Position out of bounds");
        }

        return new ChessPosition(row, col);
    }

    private String formatPosition(ChessPosition position) {
        char col = (char) ('a' + position.getColumn() - 1);
        return col + String.valueOf(position.getRow());
    }

    private ChessPiece.PieceType parsePromotionPiece(String piece) {
        return switch (piece.toUpperCase()) {
            case "Q" -> ChessPiece.PieceType.QUEEN;
            case "R" -> ChessPiece.PieceType.ROOK;
            case "B" -> ChessPiece.PieceType.BISHOP;
            case "N" -> ChessPiece.PieceType.KNIGHT;
            default -> throw new IllegalArgumentException("Invalid promotion piece");
        };
    }
} 