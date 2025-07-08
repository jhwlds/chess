package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;
    private ChessPosition enPassantTarget;
    private boolean whiteKingMoved, blackKingMoved;
    private boolean whiteQueensideRookMoved, whiteKingsideRookMoved;
    private boolean blackQueensideRookMoved, blackKingsideRookMoved;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
        this.enPassantTarget = null;
        board.setGame(this);
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

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            addCastlingMoves(possibleMoves, startPosition, piece.getTeamColor());
        }

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
        ChessPosition end = move.getEndPosition();
        ChessPiece.PieceType promote = move.getPromotionPiece();

        if (piece.getPieceType() == ChessPiece.PieceType.KING && Math.abs(start.getColumn() - end.getColumn()) == 2) {
            doCastleMove(board, piece, start, end);
            return;
        }

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                end.equals(enPassantTarget) &&
                board.getPiece(end) == null &&
                Math.abs(start.getColumn() - end.getColumn()) == 1) {
            int captureRow = (moverTeam == TeamColor.WHITE) ? end.getRow() - 1 : end.getRow() + 1;
            board.addPiece(new ChessPosition(captureRow, end.getColumn()), null);
        }

        board.addPiece(start, null);
        board.addPiece(end, promote != null ? new ChessPiece(moverTeam, promote) : piece);

        updateMovedFlags(piece, start);
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
        return isSquareAttacked(kingPos, enemy, board);
    }

    private boolean isSquareAttacked(ChessPosition pos, TeamColor enemy, ChessBoard board) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece == null || piece.getTeamColor() != enemy) {continue;}
                for (ChessMove m : piece.pieceMoves(board, new ChessPosition(row, col))) {
                    if (m.getEndPosition().equals(pos)) {
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

    private void addCastlingMoves(Collection<ChessMove> moves, ChessPosition kingPos, TeamColor color) {
        int row = kingPos.getRow();
        if (isInCheck(color)) {
            return;
        }
        if ((color == TeamColor.WHITE ? whiteKingMoved : blackKingMoved)) {
            return;
        }
        if (canCastle(color, true, row)) {
            moves.add(new ChessMove(kingPos, new ChessPosition(row, 7), null));
        }
        if (canCastle(color, false, row)) {
            moves.add(new ChessMove(kingPos, new ChessPosition(row, 3), null));
        }
    }

    private boolean canCastle(TeamColor color, boolean kingSide, int row) {
        boolean rookMoved = switch (color) {
            case WHITE -> kingSide ? whiteKingsideRookMoved : whiteQueensideRookMoved;
            case BLACK -> kingSide ? blackKingsideRookMoved : blackQueensideRookMoved;
        };
        if (rookMoved) {
            return false;
        }

        int rookCol = kingSide ? 8 : 1;
        ChessPosition rookPos = new ChessPosition(row, rookCol);
        ChessPiece rook = board.getPiece(rookPos);
        if (rook == null ||
                rook.getPieceType() != ChessPiece.PieceType.ROOK ||
                rook.getTeamColor() != color) {
            return false;
        }

        int start = Math.min(5, rookCol) + 1;
        int end = Math.max(5, rookCol) - 1;
        for (int c = start; c <= end; c++) {
            if (board.getPiece(new ChessPosition(row, c)) != null) {
                return false;
            }
        }

        TeamColor enemy = (color == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        int[] colsToCheck = kingSide ? new int[]{6, 7} : new int[]{4, 3};
        for (int col : colsToCheck) {
            if (isSquareAttacked(new ChessPosition(row, col), enemy, board)) {
                return false;
            }
        }
        return true;
    }

    private void doCastleMove(ChessBoard board, ChessPiece king, ChessPosition from, ChessPosition to) {
        int row = from.getRow();
        boolean kingSide = to.getColumn() == 7;
        ChessPosition rookFrom = kingSide ? new ChessPosition(row, 8) : new ChessPosition(row, 1);
        ChessPosition rookTo = kingSide ? new ChessPosition(row, 6) : new ChessPosition(row, 4);

        board.addPiece(from, null);
        board.addPiece(rookFrom, null);
        board.addPiece(to, king);
        board.addPiece(rookTo, new ChessPiece(king.getTeamColor(), ChessPiece.PieceType.ROOK));

        if (king.getTeamColor() == TeamColor.WHITE) {
            whiteKingMoved = true;
            if (kingSide) {
                whiteKingsideRookMoved = true;
            } else {
                whiteQueensideRookMoved = true;
            }
        } else {
            blackKingMoved = true;
            if (kingSide) {
                blackKingsideRookMoved = true;
            } else {
                blackQueensideRookMoved = true;
            }
        }
    }

    private void updateMovedFlags(ChessPiece piece, ChessPosition from) {
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                whiteKingMoved = true;
            } else {
                blackKingMoved = true;
            }
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            int row = from.getRow();
            int col = from.getColumn();
            if (piece.getTeamColor() == TeamColor.WHITE) {
                if (row == 1 && col == 1) {
                    whiteQueensideRookMoved = true;
                }
                if (row == 1 && col == 8) {
                    whiteKingsideRookMoved = true;
                }
            } else {
                if (row == 8 && col == 1) {
                    blackQueensideRookMoved = true;
                }
                if (row == 8 && col == 8) {
                    blackKingsideRookMoved = true;
                }
            }
        }
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
        board.setGame(this);
    }

    public ChessBoard getBoard() {
        return board;
    }

    public ChessGame copy() {
        ChessGame g = new ChessGame();
        g.setBoard(this.board.copy());
        g.setTeamTurn(this.teamTurn);
        g.setEnPassantTarget(this.enPassantTarget);
        g.whiteKingMoved = this.whiteKingMoved;
        g.blackKingMoved = this.blackKingMoved;
        g.whiteQueensideRookMoved = this.whiteQueensideRookMoved;
        g.whiteKingsideRookMoved = this.whiteKingsideRookMoved;
        g.blackQueensideRookMoved = this.blackQueensideRookMoved;
        g.blackKingsideRookMoved = this.blackKingsideRookMoved;
        return g;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (!(obj instanceof ChessGame other)) {return false;}
        return teamTurn == other.teamTurn &&
                Objects.equals(board, other.board) &&
                Objects.equals(enPassantTarget, other.enPassantTarget) &&
                whiteKingMoved == other.whiteKingMoved &&
                blackKingMoved == other.blackKingMoved &&
                whiteQueensideRookMoved == other.whiteQueensideRookMoved &&
                whiteKingsideRookMoved == other.whiteKingsideRookMoved &&
                blackQueensideRookMoved == other.blackQueensideRookMoved &&
                blackKingsideRookMoved == other.blackKingsideRookMoved;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn, enPassantTarget,
                whiteKingMoved, blackKingMoved,
                whiteQueensideRookMoved, whiteKingsideRookMoved,
                blackQueensideRookMoved, blackKingsideRookMoved);
    }
}
