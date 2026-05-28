/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.quoridor;

/**
 *
 * @author Salma Hesham
 */
import javax.swing.JFrame;

public class Quoridor {
    public static void main(String[] args) {
        // 1. Create the window frame
        JFrame frame = new JFrame("Quoridor AI Project");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 2. Create custom game board canvas
        GameBoard board = new GameBoard();
        frame.add(board); // Put the canvas inside the window frame
        
        // 3. Size it and make it visible
        frame.pack(); // Adjusts frame size to fit the panel
        frame.setLocationRelativeTo(null); // Centers the window on screen
        frame.setVisible(true);
    }
}