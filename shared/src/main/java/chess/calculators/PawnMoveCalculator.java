package chess.calculators;

import chess.*;
import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPiece currentPiece = board.getPiece(position);
        ChessGame.TeamColor myColor = currentPiece.getTeamColor();

        int direction = (myColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int setRow = (myColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (myColor == ChessGame.TeamColor.WHITE) ? 8 : 1;

        int row = position.getRow();
        int col = position.getColumn();

        int oneMove = row + direction;
        if (oneMove >= 1 && oneMove <= 8) {
            ChessPosition one = new ChessPosition(oneMove, col);

            if (board.getPiece(one) == null) {
                if (oneMove == promotionRow) {
                    for (ChessPiece.PieceType type : promotionTypes()) {
                        moves.add(new ChessMove(position, one, type));
                    }
                } else {
                    moves.add(new ChessMove(position, one, null));
                }

                if (row == setRow) {
                    int twoMove = row + (2 * direction);
                    ChessPosition two = new ChessPosition(twoMove, col);
                    if (board.getPiece(two) == null) {
                        moves.add(new ChessMove(position, two, null));
                    }
                }
            }
        }

        for (int dCol : new int[]{-1, +1}) {
            int diagCol = col + dCol;
            int diagRow = row + direction;

            if (diagCol < 1 || diagCol > 8 || diagRow < 1 || diagRow > 8) continue;

            ChessPosition diagPos = new ChessPosition(diagRow, diagCol);
            ChessPiece target = board.getPiece(diagPos);

            if (target != null && target.getTeamColor() != myColor) {
                if (diagRow == promotionRow) {
                    for (ChessPiece.PieceType type : promotionTypes()) {
                        moves.add(new ChessMove(position, diagPos, type));
                    }
                } else {
                    moves.add(new ChessMove(position, diagPos, null));
                }
            }
        }

        return moves;
    }

    private Collection<ChessPiece.PieceType> promotionTypes() {
        Collection<ChessPiece.PieceType> types = new ArrayList<>();
        types.add(ChessPiece.PieceType.QUEEN);
        types.add(ChessPiece.PieceType.ROOK);
        types.add(ChessPiece.PieceType.BISHOP);
        types.add(ChessPiece.PieceType.KNIGHT);
        return types;
    }
}
