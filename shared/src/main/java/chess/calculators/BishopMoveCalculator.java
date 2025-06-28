package chess.calculators;

import chess.*;
import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCalculator implements PieceMovesCalculator {

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
        return moves;
    }
}