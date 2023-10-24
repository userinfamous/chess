package com.mycompany.app;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {

    private JPanel chessboardPanel;

    public App() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chessboardPanel = new JPanel(new GridLayout(8, 8));
        initializeChessboard();
        add(chessboardPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeChessboard() {
        Color lightColor = new Color(255, 206, 158);
        Color darkColor = new Color(209, 139, 71);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JPanel square = new JPanel();
                square.setPreferredSize(new Dimension(60, 60));
                if ((row + col) % 2 == 0) {
                    square.setBackground(lightColor);
                } else {
                    square.setBackground(darkColor);
                }
                chessboardPanel.add(square);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new App();
        });
    }
}
