/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quoridor;

import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameBoard extends JPanel {

    private GameEngine engine = new GameEngine();

    // Hardcode layout constants for fast pixel rendering math
    private final int CELL_SIZE = 50;  // Size of an individual pawn moving square
    private final int WALL_SIZE = 10;  // Thickness of the gap channels between squares

    public GameBoard() {
        // Calculate the core 9x9 board layout space
        int totalSize = (9 * CELL_SIZE) + (8 * WALL_SIZE);

        // Add a 50px buffer zone at the bottom for text readouts and the Reset button
        this.setPreferredSize(new Dimension(totalSize, totalSize + 50));
        this.setBackground(Color.DARK_GRAY);
        this.setLayout(null);

        // 1. Configure the pop-up prompt options
        Object[] options = {"Player vs. AI", "Player vs. Player (Local)"};

        // 2. Launch selection dialog immediately when application window loads
        int choice = JOptionPane.showOptionDialog(
                null,
                "Choose your game mode:",
                "Quoridor - Select Game Mode",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        // 3. Inject selected setting into the logical game tracker
        engine.isVsAI = (choice == 0);

        // 4. Construct and configure the native Reset Game Button
        JButton resetButton = new JButton("Reset Game");
        // Anchor near bottom right corner: x=390, y=bottom text lane, width=130, height=25
        resetButton.setBounds(390, totalSize + 12, 130, 25);
        resetButton.setFocusable(false); // Eliminates potential layout focus bugs

        // Action configuration mapping for Reset Button clicks
        resetButton.addActionListener(e -> {
            engine.resetEngine(); // Erase values stored in programmatic matrices

            // Re-prompt user to choose their preferred game configuration setup
            int newChoice = JOptionPane.showOptionDialog(
                    null,
                    "Choose your game mode:",
                    "Quoridor - Select Game Mode",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );
            engine.isVsAI = (newChoice == 0);

            repaint(); // Instruct UI window thread to sweep clean and redraw canvas
        });

        // Append initialized button component layout onto this game canvas
        this.add(resetButton);

        // Connect the core board mouse coordinate monitoring systems
        this.addMouseListener(new BoardMouseListener());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw grid and pawns
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                int x = c * (CELL_SIZE + WALL_SIZE);
                int y = r * (CELL_SIZE + WALL_SIZE);

                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE);

                // Highlight available moves for the human player
                if ((engine.isVsAI && engine.isP1Turn) || !engine.isVsAI) {
                    if (engine.p1Row != 8 && engine.p2Row != 0) {
                        if (engine.isTileValidForActivePlayer(r, c)) {
                            g.setColor(new Color(241, 196, 15, 150));
                            g.fillRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);

                            g.setColor(Color.YELLOW);
                            g.drawRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
                        }
                    }
                }

                // P1 token
                if (r == engine.p1Row && c == engine.p1Col) {
                    g.setColor(Color.RED);
                    g.fillOval(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                } // P2 token
                else if (r == engine.p2Row && c == engine.p2Col) {
                    g.setColor(Color.BLUE);
                    g.fillOval(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                }
            }
        }

        // Render horizontal walls (span 2 cells + gap)
        g.setColor(Color.ORANGE);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 9; c++) {
                if (engine.hWalls[r][c]) {
                    int x = c * (CELL_SIZE + WALL_SIZE);
                    int y = r * (CELL_SIZE + WALL_SIZE) + CELL_SIZE;
                    g.fillRect(x, y, (CELL_SIZE * 2) + WALL_SIZE, WALL_SIZE);
                }
            }
        }

        // Render vertical walls
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 8; c++) {
                if (engine.vWalls[r][c]) {
                    int x = c * (CELL_SIZE + WALL_SIZE) + CELL_SIZE;
                    int y = r * (CELL_SIZE + WALL_SIZE);
                    g.fillRect(x, y, WALL_SIZE, (CELL_SIZE * 2) + WALL_SIZE);
                }
            }
        }

        // Bottom status bar strings
        g.setColor(Color.WHITE);
        int textY = (9 * CELL_SIZE) + (8 * WALL_SIZE) + 25;

        String turnText;
        if (engine.isVsAI) {
            turnText = engine.isP1Turn ? "Turn: Player 1 (Red)" : "Turn: AI (Blue)";
        } else {
            turnText = engine.isP1Turn ? "Turn: Player 1 (Red)" : "Turn: Player 2 (Blue)";
        }

        g.drawString(turnText, 15, textY);
        g.drawString("P1 Walls: " + engine.p1Walls, 220, textY);
        g.drawString((engine.isVsAI ? "AI Walls: " : "P2 Walls: ") + engine.p2Walls, 320, textY);

        // Check win conditions
        if (engine.p1Row == 8) {
            g.setColor(Color.GREEN);
            g.drawString("PLAYER 1 WINS THE MATCH!", 15, textY - 15);
        } else if (engine.p2Row == 0) {
            g.setColor(Color.GREEN);
            g.drawString((engine.isVsAI ? "THE AI AGENT WINS!" : "PLAYER 2 WINS THE MATCH!"), 15, textY - 15);
        }
    }

}
