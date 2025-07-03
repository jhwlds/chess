package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    public enum TeamColor {
        WHITE,
        BLACK
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) return null;

        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : possibleMoves) {
            ChessGame copy = this.copy();

            ChessBoard copiedBoard = copy.getBoard();
            copiedBoard.addPiece(startPosition, null);
            copiedBoard.addPiece(move.getEndPosition(),
                    (move.getPromotionPiece() != null)
                            ? new ChessPiece(piece.getTeamColor(), move.getPromotionPiece())
                            : piece);

            if (!copy.isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        ChessPiece piece = board.getPiece(start);
        if (piece == null) throw new InvalidMoveException("No piece at starting position");
        if (piece.getTeamColor() != teamTurn) throw new InvalidMoveException("Wrong team's turn");

        Collection<ChessMove> legalMoves = this.validMoves(start);
        if (legalMoves == null || !legalMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move for piece");
        }

        ChessPiece.PieceType promote = move.getPromotionPiece();
        if (promote != null) {
            board.addPiece(end, new ChessPiece(teamTurn, promote));
        } else {
            board.addPiece(end, piece);
        }

        board.addPiece(start, null);

        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = null;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null &&
                        piece.getTeamColor() == teamColor &&
                        piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPos = pos;
                    break;
                }
            }
        }

        if (kingPos == null) return false;

        TeamColor opponent = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                if (piece != null && piece.getTeamColor() == opponent) {
                    Collection<ChessMove> theirMoves = piece.pieceMoves(board, pos);
                    for (ChessMove move : theirMoves) {
                        if (move.getEndPosition().equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

            }
        }
        return isInCheckmate(teamColor);
    }

    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public ChessGame copy() {
        ChessGame newGame = new ChessGame();
        newGame.setBoard(this.board.copy());
        newGame.setTeamTurn(this.teamTurn);
        return newGame;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || getClass() != obj.getClass()) {return false;}
        ChessGame other = (ChessGame) obj;
        return Objects.equals(this.board, other.board) &&
                this.teamTurn == other.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
