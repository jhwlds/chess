package chess.calculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessGame;

import java.util.List;

public class MoveUtils {
    public static void addOffsetMoves(ChessBoard board,
                                      ChessPosition fromPos,
                                      List<ChessMove> moves,
                                      int[][] offsets,
                                      ChessGame.TeamColor color) {
        for (int[] offset : offsets) {
            int newRow = fromPos.getRow() + offset[0];
            int newCol = fromPos.getColumn() + offset[1];

            if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {continue;}

            ChessPosition to = new ChessPosition(newRow, newCol);
            ChessPiece target = board.getPiece(to);

            if (target == null || target.getTeamColor() != color) {
                moves.add(new ChessMove(fromPos, to, null));
            }
        }
    }
}

