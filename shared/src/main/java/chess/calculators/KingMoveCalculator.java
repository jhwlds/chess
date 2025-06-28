package chess.calculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPiece currentPiece = board.getPiece(position);
        ChessGame.TeamColor mycolor = currentPiece.getTeamColor();

        int [][] direction = {
                {-1, -1},
                {-1, +1},
                {+1, -1},
                {+1, +1},
        };

        for (int[] i : direction) {
            int row = position.getRow();
            int col = position.getColumn();

            while (true) {
                row += i[0];
                col += i[1];

                if (row < 1 || row > 8 || col < 1 || col > 8)
                    break;

                ChessPosition nextPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(nextPosition);

                if (target == null) {
                    moves.add(new ChessMove(position, nextPosition,null));
                } else {
                    if (target.getTeamColor() != mycolor) {
                        moves.add(new ChessMove(position, nextPosition, null));
                    }
                    break;
                }
            }
        }

        return moves;
    }
}