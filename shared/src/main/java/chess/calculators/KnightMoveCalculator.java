package chess.calculators;

import chess.*;
import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPiece currentPiece = board.getPiece(position);
        ChessGame.TeamColor myColor = currentPiece.getTeamColor();

        int[][] directions = {
                {-2, -1},
                {-2, +1},
                {-1, -2},
                {-1, +2},
                {+1, -2},
                {+1, +2},
                {+2, -1},
                {+2, +1}
        };

        for (int[] i : directions) {
            int row = position.getRow() + i[0];
            int col = position.getColumn() + i[1];

            if (row < 1 || row > 8 || col < 1 || col > 8) {continue;}

            ChessPosition nextPosition = new ChessPosition(row, col);
            ChessPiece target = board.getPiece(nextPosition);

            if (target == null || target.getTeamColor() != myColor) {
                moves.add(new ChessMove(position, nextPosition, null));
            }
        }

        return moves;
    }
}