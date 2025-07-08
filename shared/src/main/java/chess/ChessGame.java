package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;
    private ChessPosition enPassantTarget;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
        this.enPassantTarget = null;
    }

    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    public ChessPosition getEnPassantTarget() {
        return enPassantTarget;
    }

    public void setEnPassantTarget(ChessPosition pos) {
        this.enPassantTarget = pos;
    }

    public enum TeamColor {
        WHITE,
        BLACK
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {return null;}

        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : possibleMoves) {
            ChessGame copy = this.copy();
            copy.makeFakeMove(startPosition, move);

            if (!copy.isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    private void applyMoveToBoard(ChessBoard board, ChessPosition start, ChessMove move, TeamColor moverTeam, ChessPosition enPassantTarget) {
        ChessPiece piece = board.getPiece(start);
        ChessPiece.PieceType promote = move.getPromotionPiece();
        ChessPosition end = move.getEndPosition();

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                end.equals(enPassantTarget) &&
                board.getPiece(end) == null &&
                Math.abs(start.getColumn() - end.getColumn()) == 1) {
            int captureRow = (moverTeam == TeamColor.WHITE) ? end.getRow() - 1 : end.getRow() + 1;
            board.addPiece(new ChessPosition(captureRow, end.getColumn()), null);
        }

        board.addPiece(start, null);
        board.addPiece(end, promote != null ? new ChessPiece(moverTeam, promote) : piece);
    }


    private void makeFakeMove(ChessPosition start, ChessMove move) {
        applyMoveToBoard(board, start, move, teamTurn, enPassantTarget);
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);

        if (piece == null) {throw new InvalidMoveException("No piece at start");}
        if (piece.getTeamColor() != teamTurn) {throw new InvalidMoveException("Wrong team");}

        Collection<ChessMove> legalMoves = validMoves(start);
        if (legalMoves == null || !legalMoves.contains(move)) {
            throw new InvalidMoveException("Illegal move");
        }

        applyMoveToBoard(board, start, move, teamTurn, enPassantTarget);

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                Math.abs(start.getRow() - end.getRow()) == 2) {
            int row = (start.getRow() + end.getRow()) / 2;
            enPassantTarget = new ChessPosition(row, start.getColumn());
        } else {
            enPassantTarget = null;
        }

        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKingPosition(teamColor);
        if (kingPos == null) {return false;}

        TeamColor enemy = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                if (piece == null || piece.getTeamColor() != enemy) {continue;}

                Collection<ChessMove> enemyMoves = piece.pieceMoves(board, pos);
                for (ChessMove m : enemyMoves) {
                    if (m.getEndPosition().equals(kingPos)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                if (piece != null &&
                        piece.getTeamColor() == teamColor &&
                        piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return pos;
                }
            }
        }
        return null;
    }


    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && !hasAnyValidMove(teamColor);
    }

    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && !hasAnyValidMove(teamColor);
    }

    private boolean hasAnyValidMove(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(pos);
                    if (moves != null && !moves.isEmpty()) {return true;}
                }
            }
        }
        return false;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
        board.setGame(this);
    }

    public ChessBoard getBoard() {
        return board;
    }

    public ChessGame copy() {
        ChessGame newGame = new ChessGame();
        newGame.setBoard(this.board.copy());
        newGame.setTeamTurn(this.teamTurn);
        newGame.setEnPassantTarget(this.enPassantTarget);
        return newGame;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (!(obj instanceof ChessGame other)) {return false;}
        return teamTurn == other.teamTurn &&
                Objects.equals(board, other.board) &&
                Objects.equals(enPassantTarget, other.enPassantTarget);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn, enPassantTarget);
    }
}
