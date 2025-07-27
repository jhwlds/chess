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
            drawBlackPerspective(board);
        }
    }

    private static void drawWhitePerspective(ChessBoard board) {
        System.out.println("\n   a  b  c  d  e  f  g  h");
        System.out.println("  ------------------------");
        drawBoardRows(board, 8, 1, -1, 1, 8, true);
        System.out.println("  ------------------------");
        System.out.println("   a  b  c  d  e  f  g  h");
    }

    private static void drawBlackPerspective(ChessBoard board) {
        System.out.println("\n   h  g  f  e  d  c  b  a");
        System.out.println("  ------------------------");
        drawBoardRows(board, 1, 8, 1, 8, 1, false);
        System.out.println("  ------------------------");
        System.out.println("   h  g  f  e  d  c  b  a");
    }

    private static void drawBoardRows(ChessBoard board, int startRow, int endRow, int rowStep, 
                                     int startCol, int endCol, boolean whitePerspective) {
        for (int row = startRow; whitePerspective ? row >= endRow : row <= endRow; row += rowStep) {
            System.out.print(row + " |");
            for (int col = startCol; whitePerspective ? col <= endCol : col >= endCol; 
                 col += whitePerspective ? 1 : -1) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                drawSquare(piece, row, col);
            }
            System.out.println("| " + row);
        }
    }

    private static void drawSquare(ChessPiece piece, int row, int col) {
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

    private static String getPieceSymbol(ChessPiece piece) {
        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;

        return switch (piece.getPieceType()) {
            case PAWN -> isWhite ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            case ROOK -> isWhite ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case KNIGHT -> isWhite ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case BISHOP -> isWhite ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case QUEEN -> isWhite ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case KING -> isWhite ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
        };
    }
}
