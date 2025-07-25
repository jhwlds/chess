package chess;
import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition start;
    private final ChessPosition end;
    private final ChessPiece.PieceType promotion;

    public ChessMove(ChessPosition start, ChessPosition end, ChessPiece.PieceType promotion) {
        this.start = start;
        this.end = end;
        this.promotion = promotion;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return start;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return end;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotion;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (!(obj instanceof ChessMove that)) {return false;}
        return Objects.equals(start, that.start) &&
                Objects.equals(end, that.end) &&
                Objects.equals(promotion, that.promotion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, promotion);
    }

    @Override
    public String toString() {
        return start + " → " + end +
                (promotion != null ? " (promotes to " + promotion + ")" : "");
    }
}
