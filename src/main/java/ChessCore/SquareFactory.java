package ChessCore;

public class SquareFactory {

    public static Square createSquare(BoardFile file, BoardRank rank) {
        return new Square(file, rank);
    }}