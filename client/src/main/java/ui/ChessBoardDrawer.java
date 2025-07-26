package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class ChessBoardDrawer {
    public static void drawBoard(ChessGame game, boolean whitePerspective) {
        drawBoard(game.getBoard(), whitePerspective);
    }

    public static void drawBoard(ChessBoard board, boolean whitePerspective) {
        if (whitePerspective) {
            drawWhitePerspective(board);
        } else {
            drawBlackckPerspective(board);
        }
    }

    private static void drawWhitePerspective(ChessBoard board) {
        System.out.println("\n   a  b  c  d  e  f  g  h");
        System.out.println("  ------------------------");

        for (int row = 8; row >= 1; row--) {
            System.out.print(row + " |");
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                boolean isLightSquare = (row + col) % 2 == 0;
                String squareColor = isLightSquare ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_BLACK;
                String resetColor = EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;

                if (piece == null) {
                    System.out.print(squareColor + EscapeSequences.EMPTY + resetColor);
                } else {
                    String pieceSymbol = getPieceSymbol(piece);
                    String pieceColor = piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                            EscapeSequences.SET_TEXT_COLOR_RED : EscapeSequences.SET_TEXT_COLOR_BLUE;
                    System.out.print(squareColor + pieceColor + pieceSymbol + resetColor);
                }
            }
            System.out.println("| " + row);
        }

        System.out.println("  ------------------------");
        System.out.println("   a  b  c  d  e  f  g  h");
    }

    private static void drawBlackckPerspective(ChessBoard board) {}

    private static String getPieceSymbol(ChessPiece piece) {
        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;

        switch (piece.getPieceType()) {
            case PAWN:
                return isWhite ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            case ROOK:
                return isWhite ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case KNIGHT:
                return isWhite ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case BISHOP:
                return isWhite ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case QUEEN:
                return isWhite ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case KING:
                return isWhite ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            default:
                return EscapeSequences.EMPTY;
        }
    }
}
