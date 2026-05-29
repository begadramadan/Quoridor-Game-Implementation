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
    public boolean hasPathToGoal(int startR, int startC, int goalRow) {
        // Quick check: if already at the goal row, return true
        if (startR == goalRow) {
            return true;
        }

        boolean[][] visited = new boolean[9][9];
        Queue<int[]> queue = new LinkedList<>();

        // Add starting point [row, col] to queue
        queue.add(new int[]{startR, startC});
        visited[startR][startC] = true;

        // Standard directional steps: Down, Up, Right, Left
        int[] dRow = {1, -1, 0, 0};
        int[] dCol = {0, 0, 1, -1};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int cr = current[0];
            int cc = current[1];

            // If we reach any column on our targeted final row, path exists!
            if (cr == goalRow) {
                return true;
            }

            // Look at all 4 orthogonal neighboring steps
            for (int i = 0; i < 4; i++) {
                int nr = cr + dRow[i];
                int nc = cc + dCol[i];

                // Check bounds, ensure unvisited, and ensure no wall sits between current and neighbor
                if (nr >= 0 && nr < 9 && nc >= 0 && nc < 9 && !visited[nr][nc]) {
                    if (!isWallBlocking(cr, cc, nr, nc)) {
                        visited[nr][nc] = true;
                        queue.add(new int[]{nr, nc});
                    }
                }
            }
        }
        return false; // Queue empty, goal unreachable
    }

    public boolean placeWall(int r, int c, boolean isHorizontal) {
        // 1. Validate remaining wall counts
        int wallsLeft = isP1Turn ? p1Walls : p2Walls;
        if (wallsLeft <= 0) {
            return false;
        }

        // 2. Bound checks: walls cross intersections, so indices stop at 7
        if (r < 0 || r > 7 || c < 0 || c > 7) {
            return false;
        }

        // 3. Overlap checks
        if (isHorizontal) {
            if (hWalls[r][c] || hWalls[r][c + 1] || (vWalls[r][c] && vWalls[r + 1][c])) {
                return false;
            }
        } else {
            if (vWalls[r][c] || vWalls[r + 1][c] || (hWalls[r][c] && hWalls[r][c + 1])) {
                return false;
            }
        }

        // 4. Temporarily register the wall onto our matrix
        if (isHorizontal) {
            hWalls[r][c] = true;

        } else {
            vWalls[r][c] = true;

        }

        // 5. Pathfinding Validation: Ensure both players still retain a valid path to their goals
        // P1 wins at Row 8, P2 wins at Row 0
        boolean p1Safe = hasPathToGoal(p1Row, p1Col, 8);
        boolean p2Safe = hasPathToGoal(p2Row, p2Col, 0);

        if (p1Safe && p2Safe) {
            // Success! Deduct inventory and advance turn state
            if (isP1Turn) {
                p1Walls--;
            } else {
                p2Walls--;
            }
            isP1Turn = !isP1Turn;
            return true;
        } else {
            // Trapped! Revert the array modification changes immediately
            if (isHorizontal) {
                hWalls[r][c] = false;
                hWalls[r][c + 1] = false;
            } else {
                vWalls[r][c] = false;
                vWalls[r + 1][c] = false;
            }
            return false;
        }
    }

    public void makeAIMove() {
        // AI is Player 2. If it's not P2's turn, protect against misfires
        if (isP1Turn) {
            return;
        }
        int bestRow = p2Row;
        int bestCol = p2Col;
        int shortestPathLength = Integer.MAX_VALUE;

        // 4 possible target cells
        int[] dRow = {1, -1, 0, 0};
        int[] dCol = {0, 0, 1, -1};

        for (int i = 0; i < 4; i++) {
            int nextR = p2Row + dRow[i];
            int nextC = p2Col + dCol[i];

            if (isValidMove(p2Row, p2Col, nextR, nextC)) {
                // Measure step distance to goal line (Row 0)
                int distance = getBFSPathDistance(nextR, nextC, 0);
                if (distance < shortestPathLength) {
                    shortestPathLength = distance;
                    bestRow = nextR;
                    bestCol = nextC;
                }
            }

        }
        // Assign positions to engine values and switch turns
        p2Row = bestRow;
        p2Col = bestCol;
        isP1Turn = true;

    }

    // Helper method tracking step count depth instead of just true/false
    private int getBFSPathDistance(int startR, int startC, int goalRow) {
        if (startR == goalRow) {
            return 0;
        }
        boolean[][] visited = new boolean[9][9];
        Queue<int[]> queue = new LinkedList<>();

        queue.add(new int[]{startR, startC, 0}); // row, col, distance steps
        visited[startR][startC] = true;

        int[] dRow = {1, -1, 0, 0};
        int[] dCol = {0, 0, 1, -1};

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            if (curr[0] == goalRow) {
                return curr[2];
            }

            for (int i = 0; i < 4; i++) {
                int nr = curr[0] + dRow[i];
                int nc = curr[1] + dCol[i];
                if (nr >= 0 && nr < 9 && nc >= 0 && nc < 9 && !visited[nr][nc]) {
                    if (!isWallBlocking(curr[0], curr[1], nr, nc)) {
                        visited[nr][nc] = true;
                        queue.add(new int[]{nr, nc, curr[2] + 1});
                    }
                }
            }
        }
        return Integer.MAX_VALUE; // Path fully blocked
    }

    public void resetEngine() {
        // Reset Pawn Coordinates
        p1Row = 0;
        p1Col = 4;
        p2Row = 8;
        p2Col = 4;

        // Reset Wall Inventories
        p1Walls = 10;
        p2Walls = 10;

        // Reset Turn Status
        isP1Turn = true;

        // Clear the Wall Placement Matrix Arrays
        hWalls = new boolean[8][9];
        vWalls = new boolean[9][8];
    }
    
    public boolean isTileValidForActivePlayer(int targetRow, int targetCol) {
    // Determine who is currently taking their turn
    int currentRow = isP1Turn ? p1Row : p2Row;
    int currentCol = isP1Turn ? p1Col : p2Col;
    
    // Evaluate if the active pawn can legally step onto that tile
    return isValidMove(currentRow, currentCol, targetRow, targetCol);
}
    
}
