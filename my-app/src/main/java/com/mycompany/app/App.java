package com.mycompany.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class App extends JFrame {

    private JPanel chessboardPanel;
    private JButton pvpButton;
    private JButton pvaButton;
    private Chessboard chessboard;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean isWhiteTurn = true;

    class Chessboard {
        private Piece[][] board;

        public Chessboard() {
            board = new Piece[8][8];
            initializePieces();
        }

        private void initializePieces() {
            for (int i = 0; i < 8; i++) {
                board[1][i] = new Piece(Color.BLACK, "Pawn");
                board[6][i] = new Piece(Color.WHITE, "Pawn");
            }
                        // Initialize other pieces...
            board[0][0] = new Piece(Color.BLACK, "Black Rook"); // Black Rook
            board[0][7] = new Piece(Color.BLACK, "Black Rook"); // Black Rook
            board[7][0] = new Piece(Color.WHITE, "White Rook"); // White Rook
            board[7][7] = new Piece(Color.WHITE, "White Rook"); // White Rook

            board[0][1] = new Piece(Color.BLACK, "Black Knight"); // Black Knight
            board[0][6] = new Piece(Color.BLACK, "Black Knight"); // Black Knight
            board[7][1] = new Piece(Color.WHITE, "White Knight"); // White Knight
            board[7][6] = new Piece(Color.WHITE, "White Knight"); // White Knight

            board[0][2] = new Piece(Color.BLACK, "Black Bishop"); // Black Bishop
            board[0][5] = new Piece(Color.BLACK, "Black Bishop"); // Black Bishop
            board[7][2] = new Piece(Color.WHITE, "White Bishop"); // White Bishop
            board[7][5] = new Piece(Color.WHITE, "White Bishop"); // White Bishop

            board[0][3] = new Piece(Color.BLACK, "Black Queen"); // Black Queen
            board[7][3] = new Piece(Color.WHITE, "White Queen"); // White Queen

            board[0][4] = new Piece(Color.BLACK, "Black King"); // Black King
            board[7][4] = new Piece(Color.WHITE, "White King"); // White King
        }

        public Piece getPiece(int row, int col) {
            return board[row][col];
        }

        public void setPiece(int row, int col, Piece piece) {
            board[row][col] = piece;
        }
    }

    class Piece {
        private Color color;
        private String type;

        public Piece(Color color, String type) {
            this.color = color;
            this.type = type;
        }

        public Color getColor() {
            return color;
        }

        public String getType() {
            return type;
        }
    }

    public App() {
        initializeUI();
        chessboard = new Chessboard();
        resetChessboard();
    }

    private JPanel createSquare(final Piece piece, final int row, final int col) {
        final JPanel square = new JPanel();
        square.setPreferredSize(new Dimension(60, 60));
        square.setBackground((row + col) % 2 == 0 ? new Color(255, 206, 158) : new Color(209, 139, 71));

        if (piece != null) {
            JLabel label = new JLabel(piece.getType());
            label.setOpaque(true);
            label.setBackground(piece.getColor());
            square.add(label);
        }

        square.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedRow == -1 && selectedCol == -1) {
                    if (piece != null && ((isWhiteTurn && piece.getColor() == Color.WHITE) ||
                            (!isWhiteTurn && piece.getColor() == Color.BLACK))) {
                        selectedRow = row;
                        selectedCol = col;
                        square.setBackground(Color.YELLOW);

                        // Highlight valid moves
                        for (int newRow = 0; newRow < 8; newRow++) {
                            for (int newCol = 0; newCol < 8; newCol++) {
                                if (isValidMove(selectedRow, selectedCol, newRow, newCol)) {
                                    JPanel targetSquare = (JPanel) chessboardPanel.getComponent(newRow * 8 + newCol);
                                    targetSquare.setBackground(Color.GREEN);
                                }
                            }
                        }
                    }
                } else {
                    if (isValidMove(selectedRow, selectedCol, row, col)) {
                        movePiece(selectedRow, selectedCol, row, col);
                        isWhiteTurn = !isWhiteTurn;
                    }
                    selectedRow = -1;
                    selectedCol = -1;
                    resetChessboard();
                }
            }

        });

        return square;
    }

    private boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        Piece fromPiece = chessboard.getPiece(fromRow, fromCol);
        Piece toPiece = chessboard.getPiece(toRow, toCol);
        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        // Check for horizontal and vertical movement
        if (fromRow == toRow || fromCol == toCol) {
            int rowStep = (fromRow == toRow) ? 0 : (rowDiff > 0 ? 1 : -1);
            int colStep = (fromCol == toCol) ? 0 : (colDiff > 0 ? 1 : -1);
            for (int i = fromRow + rowStep, j = fromCol + colStep; i != toRow || j != toCol; i += rowStep, j += colStep) {
                if (chessboard.getPiece(i, j) != null) return false;
            }

            if (fromPiece.getType().contains("Rook") || fromPiece.getType().contains("Queen")) {
                return toPiece == null || !toPiece.getColor().equals(fromPiece.getColor());
            }
        }

        // Check for diagonal movement
        if (Math.abs(rowDiff) == Math.abs(colDiff)) {
            int rowStep = rowDiff > 0 ? 1 : -1;
            int colStep = colDiff > 0 ? 1 : -1;

            for (int i = fromRow + rowStep, j = fromCol + colStep; i != toRow; i += rowStep, j += colStep) {
                if (chessboard.getPiece(i, j) != null) return false;
            }

            if (fromPiece.getType().contains("Bishop") || fromPiece.getType().contains("Queen")) {
                return toPiece == null || !toPiece.getColor().equals(fromPiece.getColor());
            }
        }
            if (fromPiece.getType().equals("Pawn")) {
                if (fromPiece.getColor() == Color.WHITE) {
                    if (toRow == fromRow - 1 && toCol == fromCol && toPiece == null) {
                        return true;
                    }
                    if (fromRow == 6 && toRow == fromRow - 2 && toCol == fromCol && toPiece == null) {
                        return true;
                    }
                    if (toRow == fromRow - 1 && Math.abs(toCol - fromCol) == 1 && toPiece != null && toPiece.getColor() == Color.BLACK) {
                        return true;
                    }
                } else if (fromPiece.getColor() == Color.BLACK) {
                    if (toRow == fromRow + 1 && toCol == fromCol && toPiece == null) {
                        return true;
                    }
                    if (fromRow == 1 && toRow == fromRow + 2 && toCol == fromCol && toPiece == null) {
                        return true;
                    }
                    if (toRow == fromRow + 1 && Math.abs(toCol - fromCol) == 1 && toPiece != null && toPiece.getColor() == Color.WHITE) {
                        return true;
                    }
                }
            } else if (fromPiece.getType().contains("Rook")) {
                if (fromRow == toRow || fromCol == toCol) {
                    return toPiece == null || !toPiece.getColor().equals(fromPiece.getColor());
                }
            } else if (fromPiece.getType().contains("Knight")) {
                if ((Math.abs(fromRow - toRow) == 2 && Math.abs(fromCol - toCol) == 1) ||
                    (Math.abs(fromRow - toRow) == 1 && Math.abs(fromCol - toCol) == 2)) {
                    return toPiece == null || !toPiece.getColor().equals(fromPiece.getColor());
                }
            } else if (fromPiece.getType().contains("Bishop")) {
                if (Math.abs(fromRow - toRow) == Math.abs(fromCol - toCol)) {
                    return toPiece == null || !toPiece.getColor().equals(fromPiece.getColor());
                }
            } else if (fromPiece.getType().contains("Queen")) {
                if (fromRow == toRow || fromCol == toCol || Math.abs(fromRow - toRow) == Math.abs(fromCol - toCol)) {
                    return toPiece == null || !toPiece.getColor().equals(fromPiece.getColor());
                }
            } else if (fromPiece.getType().contains("King")) {
                if (Math.abs(fromRow - toRow) <= 1 && Math.abs(fromCol - toCol) <= 1) {
                    return toPiece == null || !toPiece.getColor().equals(fromPiece.getColor());
                }
            }
            return false;
        }

    private void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        Piece fromPiece = chessboard.getPiece(fromRow, fromCol);
        chessboard.setPiece(toRow, toCol, fromPiece);
        chessboard.setPiece(fromRow, fromCol, null);
    }

    private void initializeUI() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel menuPanel = new JPanel();
        pvpButton = new JButton("Player vs Player");
        pvaButton = new JButton("Player vs AI");
        menuPanel.add(pvpButton);
        menuPanel.add(pvaButton);

        chessboardPanel = new JPanel(new GridLayout(8, 8));
        add(menuPanel, BorderLayout.NORTH);
        add(chessboardPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void resetChessboard() {
        chessboardPanel.removeAll();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = chessboard.getPiece(row, col);
                JPanel square = createSquare(piece, row, col);
                chessboardPanel.add(square);
            }
        }

        chessboardPanel.revalidate();
        chessboardPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App());
    }
}
