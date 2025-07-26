package ui;

import chess.ChessGame;
import chess.ChessPiece;

public class ChessBoardDrawer {
    public static void drawBoard(ChessGame game, boolean perspective) {}

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
