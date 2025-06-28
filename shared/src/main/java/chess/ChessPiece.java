package chess;
import chess.calculators.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;


/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    private final ChessGame.TeamColor teamColor;
    private final PieceType pieceType;

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    public PieceType getPieceType() {
        return pieceType;
    }


    /**
     * @return which type of chess piece this piece is
     */
    public ChessPiece(ChessGame.TeamColor teamColor, PieceType pieceType) {
        this.teamColor = teamColor;
        this.pieceType = pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (pieceType) {
            case BISHOP -> new BishopMoveCalculator().pieceMoves(board, myPosition);
            case KING ->  new KingMoveCalculator().pieceMoves(board, myPosition);
            case KNIGHT -> new KnightMoveCalculator().pieceMoves(board, myPosition);
            case ROOK -> new RookMoveCalculator().pieceMoves(board, myPosition);
            case QUEEN -> new QueenMoveCalculator().pieceMoves(board, myPosition);

            default -> new ArrayList<>();
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ChessPiece that))
            return false;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }

    @Override
    public String toString() {
        return teamColor + " " + pieceType;
    }
}

