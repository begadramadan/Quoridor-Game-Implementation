/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quoridor;

import java.util.LinkedList;
import java.util.Queue;

public class GameEngine {

    int p1Row = 0, p1Col = 4; //Player 1 starts at the top center  
    int p2Row = 8, p2Col = 4; //Player 2 starts at the bottom center  
    int p1Walls = 10, p2Walls = 10; //Each gets exactly 10 walls  
    boolean isP1Turn = true; //Tracks whose turn it is  
    boolean[][] hWalls = new boolean[8][9]; //Tracks placed horizontal walls
    boolean[][] vWalls = new boolean[9][8]; // Tracks placed vertical walls
    //true means Human vs AI, false means Human vs Human (Local play)
    public boolean isVsAI = true;

    
    
    
    
}
