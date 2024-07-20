
package com.mycompany.chessgui;
import ChessCore.*;
import static ChessCore.GameStatus.IN_PROGRESS;
import ChessCore.Pieces.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessGUI extends JFrame implements ChessGameObserver {
    private JButton[][] buttons;
    private Square selectedSquare;
    private ChessGame chessgame;
    private boolean isFirstClick = true;
    private JFrame undoFrame;
    private boolean undoPerformedThisTurn = false;
   

    public ChessGUI() {
        super("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       chessgame=new ClassicChessGame();
       chessgame.addObserver(this);
        setSize(400, 400);

        createBoard();
        initializePieces();
        createUndoFrame(); 
        setVisible(true);
    }
        @Override
    public void update(ChessGameObservable observable) {
        // This method will be called whenever the game state changes

        // Check if the observable is indeed a ChessGame instance
        if (observable instanceof ChessGame) {
            ChessGame chessGame = (ChessGame) observable;
           
            removeHighlights();
            
            handlePawnPromotion();
            
            updateBoard();

            
        }
    }

    private void createBoard() {
        buttons = new JButton[8][8];

        setLayout(new GridLayout(8, 8));

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setPreferredSize(new Dimension(50, 50));
                buttons[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.BLACK);

                final int row = i;
                final int col = j;
                
                add(buttons[i][j]);

                buttons[i][j].addActionListener(e -> onSquareClick(row, col));
                
            

                
            }
        }
    }
        private void createUndoFrame() {
        undoFrame = new JFrame("Undo Move");
        undoFrame.setSize(100, 100);

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> undoLastMove());

        undoFrame.add(undoButton);
        int xOffset = getWidth(); // Adjust this value as needed
    int yOffset = 0; // Adjust this value as needed
    undoFrame.setLocation(getX() + xOffset, getY() + yOffset);
        undoFrame.setVisible(true);
    }
    private void undoLastMove() {
        if (undoPerformedThisTurn) {
        JOptionPane.showMessageDialog(this, "Undo already performed this turn.");
        return;
    }
    if (chessgame.undoLastMove()) {
        
        removeHighlights();
        undoPerformedThisTurn = true; 
    } else {
        JOptionPane.showMessageDialog(this, "Cannot undo further.");
    }
}

    private void initializePieces() {
        setPieceOnSquare("WhiteRook", 0, 0);
        setPieceOnSquare("WhiteKnight", 0, 1);
        setPieceOnSquare("WhiteBishop", 0, 2);
        setPieceOnSquare("WhiteQueen", 0, 3);
        setPieceOnSquare("WhiteKing", 0, 4);
        setPieceOnSquare("WhiteBishop", 0, 5);
        setPieceOnSquare("WhiteKnight", 0, 6);
        setPieceOnSquare("WhiteRook", 0, 7);

        for (int i = 0; i < 8; i++) {
            setPieceOnSquare("WhitePawn", 1, i);
            setPieceOnSquare("BlackPawn", 6, i);
        }

        setPieceOnSquare("BlackRook", 7, 0);
        setPieceOnSquare("BlackKnight", 7, 1);
        setPieceOnSquare("BlackBishop", 7, 2);
        setPieceOnSquare("BlackQueen", 7, 3);
        setPieceOnSquare("BlackKing", 7, 4);
        setPieceOnSquare("BlackBishop", 7, 5);
        setPieceOnSquare("BlackKnight", 7, 6);
        setPieceOnSquare("BlackRook", 7, 7);
    }

    private void setPieceOnSquare(String pieceName, int row, int col) {
        try {
            String imagePath = "src/" + pieceName + ".png";
            ImageIcon pieceIcon = new ImageIcon(imagePath);

            if (pieceIcon.getImage() == null) {
                System.err.println("Image not found: " + imagePath);
                return;
            }

            Image scaledImage = pieceIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            buttons[row][col].setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage());
        }
    }
    private void handlePawnPromotion() {
    // Check if pawn promotion occurred
    Piece promotedPiece = chessgame.getPromotedPiece();
    if (promotedPiece != null) {
        // Get the row and column of the promoted piece
        int row = chessgame.getLastMove().getToSquare().getRank().ordinal();
        int col = chessgame.getLastMove().getToSquare().getFile().ordinal();
        setPieceOnSquare(getPieceName(promotedPiece), row, col);
    }
}

private void onSquareClick(int row, int col) {
    undoPerformedThisTurn = false;
    Square clickedSquare = SquareFactory.createSquare(BoardFile.values()[col], BoardRank.values()[row]);

    if (isFirstClick) {
     //dy bne3melha 3shan nfara2 mabein awel w tany dosa
        selectedSquare = clickedSquare;
        highlightValidMoves(selectedSquare);
        isFirstClick = false;
    } else {
        //dy 5alas tany dosa
        Move move = MoveFactory.createMove(selectedSquare, clickedSquare);

        //bnshoof lw el 7araka sa7ee7a wla la
        
        if (isValidMove(move)) {
            chessgame.makeMove(move);
            
            
            

        if (Utilities.isInCheck(chessgame.getWhoseTurn(),chessgame.getBoard())) {
            Square square= Utilities.getKingSquare(chessgame.getWhoseTurn(),chessgame.getBoard());
            int rowe = square.getRank().ordinal(); 
            int cole = square.getFile().ordinal();
            buttons[rowe][cole].setBackground(Color.RED);
        }
            
           
            //bn8ayyar shakl el GUI
            

            //bnclear el square eli dosna 3aleih awel mara 3shan el 7arakat eli fel mosta2bal tet3amal
            selectedSquare = null;
            isFirstClick = true;
        } else {
            JOptionPane.showMessageDialog(this, "Invalid move!");
            removeHighlights();
            GameStatus gameStatus = chessgame.getGameStatus(); 
            if (Utilities.isInCheck(chessgame.getWhoseTurn(),chessgame.getBoard())&& !gameStatus.equals(IN_PROGRESS)) {
            Square square= Utilities.getKingSquare(chessgame.getWhoseTurn(),chessgame.getBoard());
            int rowe = square.getRank().ordinal(); 
            int cole = square.getFile().ordinal();
            buttons[rowe][cole].setBackground(Color.RED);}
            isFirstClick=true;
        }
    }
}
private void updateBoard() {
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            Square square = SquareFactory.createSquare(BoardFile.values()[j], BoardRank.values()[i]);
            Piece piece = chessgame.getBoard().getPieceAtSquare(square);
            setPieceOnSquare(getPieceName(piece), i, j);
        }
    }

    //bnclear el square 3shan n3raf n3mel 7arakat tanya
    selectedSquare = null;
    
    GameStatus gameStatus = chessgame.getGameStatus();
    switch (gameStatus) {
        case WHITE_WON:
            showGameResult("White wins!");
            break;
        case BLACK_WON:
            showGameResult("Black wins!");
            break;
        case STALEMATE:
            showGameResult("Stalemate! The game is a draw.");
            break;
        case INSUFFICIENT_MATERIAL:
            showGameResult("Insufficient material! The game is a draw.");
            break;

    }
    
}
    private void showGameResult(String message) {
    JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);

    //lw el game 5elset bnebda2 game gdeed
    chessgame.removeObserver(this);
    chessgame = new ClassicChessGame();
    chessgame.addObserver(this);

    //bnkawen el board men awel w gdeed
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            setPieceOnSquare("", i, j);
        }
    }
    initializePieces(); //bnzabat el game el gdeeda
    removeHighlights();
}
    private String getPieceName(Piece piece) {
        //bnorbot el piece be esmaha
        if (piece instanceof Rook) {
            return piece.getOwner() == Player.WHITE ? "WhiteRook" : "BlackRook";
        } else if (piece instanceof Knight) {
            return piece.getOwner() == Player.WHITE ? "WhiteKnight" : "BlackKnight";
        } else if (piece instanceof Bishop) {
            return piece.getOwner() == Player.WHITE ? "WhiteBishop" : "BlackBishop";
        } else if (piece instanceof Queen) {
            return piece.getOwner() == Player.WHITE ? "WhiteQueen" : "BlackQueen";
        } else if (piece instanceof King) {
            return piece.getOwner() == Player.WHITE ? "WhiteKing" : "BlackKing";
        } else if (piece instanceof Pawn) {
            return piece.getOwner() == Player.WHITE ? "WhitePawn" : "BlackPawn";
        } else {
            return null;
        }
    }
private boolean isValidMove(Move move) {
    return chessgame.isValidMove(move);
}
 private void removeHighlights() {
        //bnsheel el highlight lw el move et3amalet aw lw invalid
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                buttons[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.BLACK);
            }
        }
    }

private void highlightValidMoves(Square selectedSquare) {
        List<Square> validMoves = chessgame.getAllValidMovesFromSquare(selectedSquare);

        for (Square validMove : validMoves) {
            int row = validMove.getRank().ordinal();
            int col = validMove.getFile().ordinal();
            buttons[row][col].setBackground(Color.GREEN); //benhighlight el valid moves
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChessGUI());
    }
}
