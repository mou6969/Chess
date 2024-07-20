package ChessCore.Pieces;

import ChessCore.Square;
import ChessCore.Move;
import ChessCore.ChessBoard;
import ChessCore.Player;
import ChessCore.ChessGame;

public abstract class Piece {
    private final Player owner;

    protected Piece(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }
    

    public abstract boolean isValidMove(Move move, ChessGame game);
    public abstract boolean isAttackingSquare(Square pieceSquare, Square squareUnderAttack, ChessBoard board);
}
