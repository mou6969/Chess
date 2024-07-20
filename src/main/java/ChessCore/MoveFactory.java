package ChessCore;

public class MoveFactory {

    public static Move createMove(Square fromSquare, Square toSquare) {
        return new Move(fromSquare, toSquare);
    }

    public static Move createMove(Square fromSquare, Square toSquare, PawnPromotion pawnPromotion) {
        return new Move(fromSquare, toSquare, pawnPromotion);
    }


}
