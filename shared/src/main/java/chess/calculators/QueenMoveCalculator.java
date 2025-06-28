package chess.calculators;

import chess.*;
import java.util.ArrayList;
import java.util.Collection;

public class QueenMoveCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        moves.addAll(new RookMoveCalculator().pieceMoves(board, position));
        moves.addAll(new BishopMoveCalculator().pieceMoves(board, position));

        return moves;
    }
}

