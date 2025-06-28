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

        return moves;
    }
}

