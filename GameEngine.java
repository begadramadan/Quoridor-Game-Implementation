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

//receives the coordinates directly from the Mouse Listener
    public boolean tryMovePlayer(int targetRow, int targetCol) {
        // 1. Get current player's coordinates
        int currentRow = isP1Turn ? p1Row : p2Row;
        int currentCol = isP1Turn ? p1Col : p2Col;

        // 2. Validate the move using your logic
        if (isValidMove(currentRow, currentCol, targetRow, targetCol)) {
            // 3. Update the correct primitive variables directly
            if (isP1Turn) {
                p1Row = targetRow;
                p1Col = targetCol;
            } else {
                p2Row = targetRow;
                p2Col = targetCol;
            }

            // 4. Switch the turn and return success 
            isP1Turn = !isP1Turn;
            return true;
        }
        return false; // Move was illegal
    }

    public boolean isWallBlocking(int r1, int c1, int r2, int c2) {
        // 1. Moving DOWN
        if (r2 == r1 + 1 && c1 == c2) {
            return hWalls[r1][c1] || (c1 > 0 && hWalls[r1][c1 - 1]);
        }
        // 2. Moving UP
        if (r2 == r1 - 1 && c1 == c2) {
            return hWalls[r2][c1] || (c1 > 0 && hWalls[r2][c1 - 1]);
        }
        // 3. Moving RIGHT
        if (c2 == c1 + 1 && r1 == r2) {
            return vWalls[r1][c1] || (r1 > 0 && vWalls[r1 - 1][c1]);
        }
        // 4. Moving LEFT
        if (c2 == c1 - 1 && r1 == r2) {
            return vWalls[r1][c2] || (r1 > 0 && vWalls[r1 - 1][c2]);
        }
        return false; // No wall blocking the path
    }

    private boolean isValidMove(int r1, int c1, int r2, int c2) {
        // 1. Board Boundary Check
        if (r2 < 0 || r2 > 8 || c2 < 0 || c2 > 8) {
            return false;
        }

        // 2. Destination target square cannot be occupied by anyone
        if (r2 == p1Row && c2 == p1Col) {
            return false;
        }
        if (r2 == p2Row && c2 == p2Col) {
            return false;
        }

        int rowDist = Math.abs(r2 - r1);
        int colDist = Math.abs(c2 - c1);

        // Identify current opponent's exact position
        int oppRow = isP1Turn ? p2Row : p1Row;
        int oppCol = isP1Turn ? p2Col : p1Col;

        // ==========================================================
        // CASE A: STANDARD MOVE (1 Square Away Orthogonally)
        // ==========================================================
        if ((rowDist == 1 && colDist == 0) || (rowDist == 0 && colDist == 1)) {
            return !isWallBlocking(r1, c1, r2, c2);
        }

        // ==========================================================
        // CASE B: STRAIGHT PAWN JUMP (2 Squares Away Orthogonally)
        // ==========================================================
        if ((rowDist == 2 && colDist == 0) || (rowDist == 0 && colDist == 2)) {
            int midRow = (r1 + r2) / 2;
            int midCol = (c1 + c2) / 2;

            if (midRow == oppRow && midCol == oppCol) {
                return !isWallBlocking(r1, c1, midRow, midCol)
                        && !isWallBlocking(midRow, midCol, r2, c2);
            }
        }

        // ==========================================================
        // CASE C: DIAGONAL JUMP (Blocked Straight Jump Contigency)
        // ==========================================================
        if (rowDist == 1 && colDist == 1) {
            // Condition 1: Opponent is adjacent to you HORIZONTALLY
            if (r1 == oppRow && Math.abs(c1 - oppCol) == 1) {
                // Find out where the straight landing square would have been
                int straightJumpCol = oppCol + (oppCol - c1);

                // Check if the straight jump is blocked by going out of bounds OR a wall behind opponent
                boolean straightJumpBlocked = (straightJumpCol < 0 || straightJumpCol > 8)
                        || isWallBlocking(oppRow, oppCol, oppRow, straightJumpCol);

                if (straightJumpBlocked) {
                    // To land at (r2, c2) diagonally, there must be no wall between you and opponent,
                    // AND no wall between the opponent and your diagonal landing square.
                    if (!isWallBlocking(r1, c1, oppRow, oppCol) && !isWallBlocking(oppRow, oppCol, r2, c2)) {
                        return true;
                    }
                }
            }

            // Condition 2: Opponent is adjacent to you VERTICALLY
            if (c1 == oppCol && Math.abs(r1 - oppRow) == 1) {
                // Find out where the straight landing square would have been
                int straightJumpRow = oppRow + (oppRow - r1);

                // Check if the straight jump is blocked by going out of bounds OR a wall behind opponent
                boolean straightJumpBlocked = (straightJumpRow < 0 || straightJumpRow > 8)
                        || isWallBlocking(oppRow, oppCol, straightJumpRow, oppCol);

                if (straightJumpBlocked) {
                    // To land at (r2, c2) diagonally, there must be no wall between you and opponent,
                    // AND no wall between the opponent and your diagonal landing square.
                    if (!isWallBlocking(r1, c1, oppRow, oppCol) && !isWallBlocking(oppRow, oppCol, r2, c2)) {
                        return true;
                    }
                }
            }
        }

        return false; // Any other move configuration is completely illegal
    }

    
    
    
    
}
