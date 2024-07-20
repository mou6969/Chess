package ChessCore;
import ChessCore.Pieces.*;
public class ChessBoardFactory {

    public static ChessBoard createChessBoard(Piece[][] initialBoard) {
        return new ChessBoard(initialBoard);
    }
    public static ChessBoard createChessBoard(ChessBoard originalBoard) {
        return new ChessBoard(originalBoard);
    }
    }
    



