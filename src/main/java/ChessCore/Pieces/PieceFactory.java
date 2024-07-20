package ChessCore.Pieces;

import ChessCore.Player;

public class PieceFactory {
    public static Piece createPawn(Player owner) {
        return new Pawn(owner);
    }

    public static Piece createRook(Player owner) {
        return new Rook(owner);
    }

    public static Piece createKnight(Player owner) {
        return new Knight(owner);
    }

    public static Piece createBishop(Player owner) {
        return new Bishop(owner);
    }

    public static Piece createQueen(Player owner) {
        return new Queen(owner);
    }

    public static Piece createKing(Player owner) {
        return new King(owner);
    }


}
