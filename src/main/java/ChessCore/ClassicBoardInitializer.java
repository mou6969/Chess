package ChessCore;

import ChessCore.Pieces.*;


public final class ClassicBoardInitializer implements BoardInitializer {
    private static final BoardInitializer instance = new ClassicBoardInitializer();

    private ClassicBoardInitializer() {
    }

    public static BoardInitializer getInstance() {
        return instance;
    }

    @Override
    public Piece[][] initialize() {
        Piece[][] initialState = {
            {PieceFactory.createRook(Player.WHITE), PieceFactory.createKnight(Player.WHITE), PieceFactory.createBishop(Player.WHITE), PieceFactory.createQueen(Player.WHITE), PieceFactory.createKing(Player.WHITE), PieceFactory.createBishop(Player.WHITE), PieceFactory.createKnight(Player.WHITE), PieceFactory.createRook(Player.WHITE)},
            {PieceFactory.createPawn(Player.WHITE), PieceFactory.createPawn(Player.WHITE), PieceFactory.createPawn(Player.WHITE), PieceFactory.createPawn(Player.WHITE), PieceFactory.createPawn(Player.WHITE), PieceFactory.createPawn(Player.WHITE), PieceFactory.createPawn(Player.WHITE), PieceFactory.createPawn(Player.WHITE)},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {PieceFactory.createPawn(Player.BLACK), PieceFactory.createPawn(Player.BLACK), PieceFactory.createPawn(Player.BLACK), PieceFactory.createPawn(Player.BLACK), PieceFactory.createPawn(Player.BLACK), PieceFactory.createPawn(Player.BLACK), PieceFactory.createPawn(Player.BLACK), PieceFactory.createPawn(Player.BLACK)},
            {PieceFactory.createRook(Player.BLACK), PieceFactory.createKnight(Player.BLACK), PieceFactory.createBishop(Player.BLACK), PieceFactory.createQueen(Player.BLACK), PieceFactory.createKing(Player.BLACK), PieceFactory.createBishop(Player.BLACK), PieceFactory.createKnight(Player.BLACK), PieceFactory.createRook(Player.BLACK)}
        };
        return initialState;
    }
}
