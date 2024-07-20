package ChessCore.Pieces;

import ChessCore.Square;
import ChessCore.Move;
import ChessCore.ChessBoard;
import ChessCore.Player;
import ChessCore.ChessGame;
import ChessCore.MoveFactory;

public final class Bishop extends Piece {
    public Bishop(Player owner) {
        super(owner);
    }

    @Override
    public boolean isValidMove(Move move, ChessGame game) {
        return move.isDiagonalMove() && !game.isTherePieceInBetween(move);
    }

    @Override
    public boolean isAttackingSquare(Square pieceSquare, Square squareUnderAttack, ChessBoard board) {
        Move move = MoveFactory.createMove(pieceSquare, squareUnderAttack);
        return move.isDiagonalMove() && !board.isTherePieceInBetween(move);
    }
}
