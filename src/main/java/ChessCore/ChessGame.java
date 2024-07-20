
package ChessCore;

import ChessCore.Pieces.Bishop;
import ChessCore.Pieces.King;
import ChessCore.Pieces.Knight;
import ChessCore.Pieces.Pawn;
import ChessCore.Pieces.Piece;
import ChessCore.Pieces.Rook;
import ChessCore.Pieces.PieceFactory;


import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import java.util.Stack;



public abstract class ChessGame implements ChessGameObservable{
    private final ChessBoard board;
    private GameStatus gameStatus = GameStatus.IN_PROGRESS;
    private Player whoseTurn = Player.WHITE;
    private Piece capturedPiece;
    private Square enPassantSquare;
    private List<ChessGameObserver> observers = new ArrayList<>();


    private Move lastMove;
    private boolean canWhiteCastleKingSide = true;
    private boolean canWhiteCastleQueenSide = true;
    private boolean canBlackCastleKingSide = true;
    private boolean canBlackCastleQueenSide = true;
    private Piece promotedPiece;
    private Stack<Move> moveHistory = new Stack<>();


    public ChessGame(BoardInitializer boardInitializer) {
        this.board = ChessBoardFactory.createChessBoard(boardInitializer.initialize());
        this.moveHistory = new Stack<>();
        this.enPassantSquare=null;
    }
        @Override
    public void addObserver(ChessGameObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(ChessGameObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (ChessGameObserver observer : observers) {
            observer.update(this);
        }
    }
     public Piece getPromotedPiece() {
        return promotedPiece;
    }
    public Square getEnPassantSquare() {
    return enPassantSquare;
}

public void setEnPassantSquare(Square enPassantSquare) {
    this.enPassantSquare = enPassantSquare;
}

    public boolean isCanWhiteCastleKingSide() {
        return canWhiteCastleKingSide;
    }

    public boolean isCanWhiteCastleQueenSide() {
        return canWhiteCastleQueenSide;
    }

    public boolean isCanBlackCastleKingSide() {
        return canBlackCastleKingSide;
    }

    public boolean isCanBlackCastleQueenSide() {
        return canBlackCastleQueenSide;
    }

    public boolean isValidMove(Move move) {
        if (isGameEnded()) {
            return false;
        }

        Piece pieceAtFrom = board.getPieceAtSquare(move.getFromSquare());
        if (pieceAtFrom == null || pieceAtFrom.getOwner() != whoseTurn  || !pieceAtFrom.isValidMove(move, this)) {
            return false;
        }

        Piece pieceAtTo = board.getPieceAtSquare(move.getToSquare());
        // A player can't capture his own piece.
        if (pieceAtTo != null && pieceAtTo.getOwner() == whoseTurn) {
            return false;
        }

        return isValidMoveCore(move);
    }

    public Move getLastMove() {
        return lastMove;
    }

    public Player getWhoseTurn() {
        return whoseTurn;
    }

    public ChessBoard getBoard() {
        return board;
    }

    protected abstract boolean isValidMoveCore(Move move);

    public boolean isTherePieceInBetween(Move move) {
        return board.isTherePieceInBetween(move);
    }

    public boolean hasPieceIn(Square square) {
        return board.getPieceAtSquare(square) != null;
    }

    public boolean hasPieceInSquareForPlayer(Square square, Player player) {
        Piece piece = board.getPieceAtSquare(square);
        return piece != null && piece.getOwner() == player;
    }

    public boolean makeMove(Move move) {
        if (!isValidMove(move)) {
            return false;
        }

        Square fromSquare = move.getFromSquare();
        Square toSquare = move.getToSquare();
        Piece fromPiece = board.getPieceAtSquare(fromSquare);
        
        capturedPiece = board.getPieceAtSquare(toSquare);
        

        // If the king has moved, castle is not allowed.
        if (fromPiece instanceof King) {
            if (fromPiece.getOwner() == Player.WHITE) {
                canWhiteCastleKingSide = false;
                canWhiteCastleQueenSide = false;
            } else {
                canBlackCastleKingSide = false;
                canBlackCastleQueenSide = false;
            }
        }

        // If the rook has moved, castle is not allowed on that specific side..
        if (fromPiece instanceof Rook) {
            if (fromPiece.getOwner() == Player.WHITE) {
                if (fromSquare.getFile() == BoardFile.A && fromSquare.getRank() == BoardRank.FIRST) {
                    canWhiteCastleQueenSide = false;
                } else if (fromSquare.getFile() == BoardFile.H && fromSquare.getRank() == BoardRank.FIRST) {
                    canWhiteCastleKingSide = false;
                }
            } else {
                if (fromSquare.getFile() == BoardFile.A && fromSquare.getRank() == BoardRank.EIGHTH) {
                    canBlackCastleQueenSide = false;
                } else if (fromSquare.getFile() == BoardFile.H && fromSquare.getRank() == BoardRank.EIGHTH) {
                    canBlackCastleKingSide = false;
                }
            }
        }

        // En-passant.
    if (fromPiece instanceof Pawn &&
            move.getAbsDeltaX() == 1 &&
            !hasPieceIn(move.getToSquare())) {
        enPassantSquare = SquareFactory.createSquare(move.getToSquare().getFile(), fromSquare.getRank());

        // Remove the captured pawn in en passant
        Square capturedPawnSquare = SquareFactory.createSquare(move.getToSquare().getFile(), fromSquare.getRank());
        board.setPieceAtSquare(capturedPawnSquare, null);
    } else {
        enPassantSquare = null;
    }
    move.setEnPassantSquare(enPassantSquare);


// Promotion
if (fromPiece instanceof Pawn) {
    BoardRank toSquareRank = move.getToSquare().getRank();
    if (toSquareRank == BoardRank.FIRST || toSquareRank == BoardRank.EIGHTH) {
        Player playerPromoting = toSquareRank == BoardRank.EIGHTH ? Player.WHITE : Player.BLACK;

        // Ask the user for pawn promotion choice
        String[] options = {"Queen", "Rook", "Knight", "Bishop"};
        String promotionChoice = (String) JOptionPane.showInputDialog(
                null,
                "Choose a piece for promotion:",
                "Pawn Promotion",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        // Determine the piece type based on user choice
        PawnPromotion promotion;
        switch (promotionChoice) {
            case "Queen":
                fromPiece = PieceFactory.createQueen(playerPromoting);
                promotedPiece=fromPiece;
                promotion = PawnPromotion.Queen;
                break;
            case "Rook":
                fromPiece = PieceFactory.createRook(playerPromoting);
                promotedPiece=fromPiece;
                promotion = PawnPromotion.Rook;
                break;
            case "Knight":
                fromPiece = PieceFactory.createKnight(playerPromoting);
                promotedPiece=fromPiece;
                promotion = PawnPromotion.Knight;
                break;
            case "Bishop":
                fromPiece = PieceFactory.createBishop(playerPromoting);
                promotedPiece=fromPiece;
                promotion = PawnPromotion.Bishop;
                break;
            default:
                throw new RuntimeException("Invalid promotion choice");
        }

        // Set the promotion type in the move
        move.setPawnPromotion(promotion);
    }
}

        // Castle
        if (fromPiece instanceof King &&
                move.getAbsDeltaX() == 2) {

            
            if (toSquare.getFile() == BoardFile.G && toSquare.getRank() == BoardRank.FIRST) {
                // White king-side castle.
                // Rook moves from H1 to F1
                Square h1 = SquareFactory.createSquare(BoardFile.H, BoardRank.FIRST);
                Square f1 = SquareFactory.createSquare(BoardFile.F, BoardRank.FIRST);
                Piece rook = board.getPieceAtSquare(h1);
                board.setPieceAtSquare(h1, null);
                board.setPieceAtSquare(f1, rook);
            } else if (toSquare.getFile() == BoardFile.G && toSquare.getRank() == BoardRank.EIGHTH) {
                // Black king-side castle.
                // Rook moves from H8 to F8
                Square h8 = SquareFactory.createSquare(BoardFile.H, BoardRank.EIGHTH);
                Square f8 = SquareFactory.createSquare(BoardFile.F, BoardRank.EIGHTH);
                Piece rook = board.getPieceAtSquare(h8);
                board.setPieceAtSquare(h8, null);
                board.setPieceAtSquare(f8, rook);
            } else if (toSquare.getFile() == BoardFile.C && toSquare.getRank() == BoardRank.FIRST) {
                // White queen-side castle.
                // Rook moves from A1 to D1
                Square a1 = SquareFactory.createSquare(BoardFile.A, BoardRank.FIRST);
                Square d1 = SquareFactory.createSquare(BoardFile.D, BoardRank.FIRST);
                Piece rook = board.getPieceAtSquare(a1);
                board.setPieceAtSquare(a1, null);
                board.setPieceAtSquare(d1, rook);
            } else if (toSquare.getFile() == BoardFile.C && toSquare.getRank() == BoardRank.EIGHTH) {
                // Black queen-side castle.
                // Rook moves from A8 to D8
                Square a8 = SquareFactory.createSquare(BoardFile.A, BoardRank.EIGHTH);
                Square d8 = SquareFactory.createSquare(BoardFile.D, BoardRank.EIGHTH);
                Piece rook = board.getPieceAtSquare(a8);
                board.setPieceAtSquare(a8, null);
                board.setPieceAtSquare(d8, rook);
            }
        }

        board.setPieceAtSquare(fromSquare, null);
        board.setPieceAtSquare(move.getToSquare(), fromPiece);


        whoseTurn = Utilities.revertPlayer(whoseTurn);
        lastMove = move;
        
        updateGameStatus();
        notifyObservers();
        moveHistory.push(move);

        return true;
    }

    private void updateGameStatus() {
        Player whoseTurn = getWhoseTurn();
        boolean isInCheck = Utilities.isInCheck(whoseTurn, getBoard());
        boolean hasAnyValidMoves = hasAnyValidMoves();
        if (isInCheck) {
            if (!hasAnyValidMoves && whoseTurn == Player.WHITE) {
                gameStatus = GameStatus.BLACK_WON;
            } else if (!hasAnyValidMoves && whoseTurn == Player.BLACK) {
                gameStatus = GameStatus.WHITE_WON;
            } else if (whoseTurn == Player.WHITE) {
                gameStatus = GameStatus.WHITE_UNDER_CHECK;
            } else {
                gameStatus = GameStatus.BLACK_UNDER_CHECK;
            }
        } else if (!hasAnyValidMoves) {
            gameStatus = GameStatus.STALEMATE;
        }
        else {
            gameStatus = GameStatus.IN_PROGRESS;
        }

        // Note: Insufficient material can happen while a player is in check. Consider this scenario:
        // Board with two kings and a lone pawn. The pawn is promoted to a Knight with a check.
        // In this game, a player will be in check but the game also ends as insufficient material.
        // For this case, we just mark the game as insufficient material.
        // It might be better to use some sort of a "Flags" enum.
        // Or, alternatively, don't represent "check" in gameStatus
        // Instead, have a separate isWhiteInCheck/isBlackInCheck methods.
        if (isInsufficientMaterial()) {
            gameStatus = GameStatus.INSUFFICIENT_MATERIAL;
        }

    }
public boolean undoLastMove() {
    if (moveHistory.isEmpty()) {
        return false;
    }

    lastMove = moveHistory.pop();

    // Revert the board to the state before the move
    Square fromSquare = lastMove.getFromSquare();
    Square toSquare = lastMove.getToSquare();
    Piece movedPiece = board.getPieceAtSquare(toSquare);

    // Revert castling moves
    if (movedPiece instanceof King && Math.abs(fromSquare.getFile().ordinal() - toSquare.getFile().ordinal()) == 2) {
        // Castling move, revert the rook as well
        revertCastlingMove(fromSquare, toSquare);
    }

    if (capturedPiece != null) {
        board.setPieceAtSquare(toSquare, capturedPiece);
        board.setPieceAtSquare(fromSquare, movedPiece);
        capturedPiece = null; // Reset capturedPiece after reverting
    } else {
        // Revert the moved piece
        board.setPieceAtSquare(toSquare, null);
        board.setPieceAtSquare(fromSquare, movedPiece);

        // Revert en passant capture
        if (lastMove.getAbsDeltaX() == 1 && movedPiece instanceof Pawn) {
            enPassantSquare = lastMove.getEnPassantSquare();
            Player capturedPawnOwner = Utilities.revertPlayer(whoseTurn == Player.WHITE ? Player.BLACK : Player.WHITE);
            Piece capturedPawn = PieceFactory.createPawn(capturedPawnOwner);
            board.setPieceAtSquare(enPassantSquare, capturedPawn);
        } else {
            enPassantSquare = null;
        }
    }

    // Revert special moves like pawn promotion
    if (lastMove.getPawnPromotion() != PawnPromotion.None) {
        // Pawn was promoted, revert to pawn
        Piece pawn = PieceFactory.createPawn(movedPiece.getOwner());
        board.setPieceAtSquare(fromSquare, pawn);
    }

    // Update game state variables
    enPassantSquare = lastMove.getEnPassantSquare();
    canWhiteCastleKingSide = true;
    canWhiteCastleQueenSide = true;
    canBlackCastleKingSide = true;
    canBlackCastleQueenSide = true;
    promotedPiece = null;
    whoseTurn = Utilities.revertPlayer(whoseTurn);
    
    notifyObservers();

    return true;
}



private void revertCastlingMove(Square kingFromSquare, Square kingToSquare) {
    // Revert castling move by moving the rook to its original position
    int direction = kingToSquare.getFile().ordinal() > kingFromSquare.getFile().ordinal() ? 1 : -1;
    int rookFromCol = direction > 0 ? 7 : 0; // H file for king-side, A file for queen-side
    int rookToCol = kingFromSquare.getFile().ordinal() + direction;

    Square rookFromSquare = SquareFactory.createSquare(BoardFile.values()[rookFromCol], kingFromSquare.getRank());
    Square rookToSquare = SquareFactory.createSquare(BoardFile.values()[rookToCol], kingFromSquare.getRank());

    Piece rook = board.getPieceAtSquare(rookToSquare);
    board.setPieceAtSquare(rookToSquare, null);
    board.setPieceAtSquare(rookFromSquare, rook);
}


    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public boolean isGameEnded() {
        return gameStatus == GameStatus.WHITE_WON ||
                gameStatus == GameStatus.BLACK_WON ||
                gameStatus == GameStatus.STALEMATE ||
                gameStatus == GameStatus.INSUFFICIENT_MATERIAL;
    }

    private boolean isInsufficientMaterial() {
        /*
        If both sides have any one of the following, and there are no pawns on the board:

        A lone king
        a king and bishop
        a king and knight
        */
        int whiteBishopCount = 0;
        int blackBishopCount = 0;
        int whiteKnightCount = 0;
        int blackKnightCount = 0;

        for (BoardFile file : BoardFile.values()) {
            for (BoardRank rank : BoardRank.values()) {
                Piece p = getPieceAtSquare(SquareFactory.createSquare(file, rank));
                if (p == null || p instanceof King) {
                    continue;
                }

                if (p instanceof Bishop) {
                    if (p.getOwner() == Player.WHITE) {
                        whiteBishopCount++;
                    } else {
                        blackBishopCount++;
                    }
                } else if (p instanceof Knight) {
                    if (p.getOwner() == Player.WHITE) {
                        whiteKnightCount++;
                    } else {
                        blackKnightCount++;
                    }
                } else {
                    // There is a non-null piece that is not a King, Knight, or Bishop.
                    // This can't be insufficient material.
                    return false;
                }
            }
        }

        boolean insufficientForWhite = whiteKnightCount + whiteBishopCount <= 1;
        boolean insufficientForBlack = blackKnightCount + blackBishopCount <= 1;
        return insufficientForWhite && insufficientForBlack;
    }

    private boolean hasAnyValidMoves() {
        for (BoardFile file : BoardFile.values()) {
            for (BoardRank rank : BoardRank.values()) {
                if (!getAllValidMovesFromSquare(SquareFactory.createSquare(file, rank)).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<Square> getAllValidMovesFromSquare(Square square) {
        ArrayList<Square> validMoves = new ArrayList<>();
        for (var i : BoardFile.values()) {
            for (var j : BoardRank.values()) {
                var sq = SquareFactory.createSquare(i, j);
                if (isValidMove(MoveFactory.createMove(square, sq, PawnPromotion.Queen))) {
                    validMoves.add(sq);
                }
            }
        }

        return validMoves;
    }

    public Piece getPieceAtSquare(Square square) {
        return board.getPieceAtSquare(square);
    }
}
