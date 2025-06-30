package chess.calculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMoveCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        List<ChessMove> moves = new ArrayList<>();

        int[][] knightOffsets = {
                {-2, -1},
                {-2, +1},
                {-1, -2},
                {-1, +2},
                {+1, -2},
                {+1, +2},
                {+2, -1},
                {+2, +1}
        };

        ChessPiece currentPiece = board.getPiece(position);
        ChessGame.TeamColor myColor = currentPiece.getTeamColor();

        MoveUtils.addOffsetMoves(board, position, moves, knightOffsets, myColor);

        return moves;
    }
}
