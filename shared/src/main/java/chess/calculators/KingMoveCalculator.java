package chess.calculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMoveCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        List<ChessMove> moves = new ArrayList<>();

        int[][] kingOffsets = {
                {-1, -1},
                {-1, 0},
                {-1, 1},
                { 0, -1},
                { 0, 1},
                { 1, -1},
                { 1, 0},
                { 1, 1}
        };

        ChessPiece currentPiece = board.getPiece(position);
        ChessGame.TeamColor myColor = currentPiece.getTeamColor();

        MoveUtils.addOffsetMoves(board, position, moves, kingOffsets, myColor);

        return moves;
    }
}
