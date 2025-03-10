public class Board {
    private int[][] board;

    // Constructor that initializes the board with the default values
    public Board() {
        this.board = new int[][] {
                {1, 0, 0, 2, 0, -1, 2, 0, 0, 0, 0, 1},
                {1, 0, 0, 2, 0, -1, 2, 0, 0, 0, 0, 1},
                {1, 0, 0, 2, 0, -1, 2, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, -1, 2, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, -1, 2, 0, 0, 0, 0, 0},
                {-2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2},
                {2, 0, 0, 0, 0, -1, 1, 0, 0, 0, 0, 0},
                {2, 0, 0, 0, 0, -1, 1, 0, 0, 0, 0, 0},
                {2, 0, 0, 1, 0, -1, 1, 0, 0, 0, 0, 0},
                {2, 0, 0, 1, 0, -1, 1, 0, 0, 0, 0, 2},
                {2, 0, 0, 1, 0, -1, 1, 0, 0, 0, 0, 2}
        };
    }

    // Method to print the board based on the matrix
    public  void printBoard(int[][] board) {
        System.out.println("In the following board black stones are shown as \\\"B\\\" and white stones as \\\"W\\\"");
        System.out.println("========================================");
        System.out.println("      1  2  3  4  5  |  7  8  9 10 11 12");
        System.out.println("+--------------------------------------+");

        // Print the board rows with formatted row indices
        for (int row = 0; row < board.length; row++) {
            // Format the row number with leading zeros
            System.out.print(String.format("%2d", row + 1) + " | "); // Print row index with leading zero if necessary

            for (int col = 0; col < board[row].length; col++) {
                // Handle different cases based on the value
                if (board[row][col] == 1) {
                    System.out.print(" W ");  // White player's piece
                } else if (board[row][col] == 2) {
                    System.out.print(" B ");  // Black player's piece
                } else if (board[row][col] == -1) {
                    System.out.print(" | ");  // Vertical line separator
                } else if (board[row][col] == -2) {
                    System.out.print(" - ");  // Horizontal line separator
                } else {
                    System.out.print(" . ");  // Empty space
                }
            }
            System.out.println();  // Move to the next row
        }
        System.out.println("+--------------------------------------+");
        System.out.println("      1  2  3  4  5  |  7  8  9 10 11 12");
        System.out.println("========================================");
    }


    public int[][] getMatrix() {
        return this.board;
    }


    public void printMatrix(int[][] board) {


        // Iterate through each row
        for (int i = 0; i < board.length; i++) {
            // Iterate through each column in the current row
            for (int j = 0; j < board[i].length; j++) {
                // Print each element followed by a space, so it looks like a grid
                System.out.print(board[i][j] + " ");
            }
            // After each row, print a new line
            System.out.println();
        }
    }







    public boolean isMoveValid(int startRow, int startCol, int endRow, int endCol, StartTray startTray,int playerNumber) {

        // Check if the move is within the bounds of the board
        if (endRow < 0 || endRow >= board.length || endCol < 0 || endCol >= board[0].length) {
            System.out.println("Invalid move");
            return false;
        }

        if (startTray.getStones(playerNumber) != 0) {  // Corrected method call and comparison
            // The player is trying to move a piece on the board instead of placing one from the start tray
            if (board[startRow][startCol] != 0) {
                System.out.println("Invalid move");
                return false; // Invalid move: Player must first place a piece from the start tray
            }
        }

        // Get the piece type at the starting position
        int player = board[startRow][startCol];

        // Determine the opponent's piece
        int opponent = (player == 1) ? 2 : 1;

        // Count the number of opponent pieces in the destination column (endY column)
        int opponentPieceCount = 0;

        if(endRow<5){
        // Loop through all rows in the destination column (endY)
        for (int i = 0; i < 5; i++) {
            if (board[i][endCol] == opponent) {
                opponentPieceCount++;
            }
        }} else if (endRow>5) {

            for (int i = 6; i < 11; i++) {
                if (board[i][endCol] == opponent) {
                    opponentPieceCount++;
                }

        }}

        if (opponentPieceCount == 1) {
            // Remove the opponent piece from the board
            // Assume we remove the opponent piece from the first location we find
            for (int i = 0; i < board.length; i++) {
                if (board[i][endCol] == opponent) {
                    // Remove the opponent piece by setting that position to 0
                    board[i][endCol] = 0;
                    break; // Stop after removing the first opponent piece
                }
            }

            // Add the removed opponent piece to the "start tray" (this is assumed to be a collection)
            // Let's assume the "start tray" is an array or list named "startTray"
            System.out.println("Opponent put to Tray!");
            startTray.add(opponent); // Add the opponent's piece to the start tray
        }

        // If there are more than 1 opponent pieces in the destination column, the move is invalid
        return opponentPieceCount < 1;

    }












    // Moves a piece on the board
    public void makeMove(int[][] board, int row, int col, int diceValue, int playerNumber, WinTray winTray, StartTray startTray) {
        // Determine movement direction
        int newCol = col;
        int newRow = row;
        int totalRows=10;
        int stepsRemaining = diceValue;

        // Move right for Player 1 (White)
        if (playerNumber == 1 && board[row][col] == 1) {
            while (stepsRemaining > 0) {
                newCol++;
                if (newCol == 5) {
                    newCol++;
                    newRow=row;} // Skip column 5
                if (newCol >= 12 && row < 5) {
                    newCol %= 12;
                    newRow = newRow + 6;} // Wrap around the board
                stepsRemaining--;
            }

            // **Check if White exits the board (Win condition)**
            if (row > 5 && newCol > 11) {
                System.out.println("Player 1 (White) has reached the winning tray!");
                winTray.upAmountStones(1);
                board[row][col] = 0; // Remove stone from the board
                return;
            }
        }

        // Move left for Player 2 (Black)
        else if (playerNumber == 2 && board[row][col] == 2) {
            while (stepsRemaining > 0) {
                newCol--;
                if (newCol == 5) {
                    newCol--;
                    newRow=row;} // Skip column 5
                if (newCol < 0 && row>5) {
                    newCol = (newCol + 12) % 12;
                    newRow = (newRow -6) % totalRows;}// Wrap around the board
                stepsRemaining--;
            }

            // **Check if Black exits the board (Win condition)**
            if (row < 5 && newCol < 0) {
                System.out.println("Player 2 (Black) has reached the winning tray!");
                winTray.upAmountStones(-1);
                board[row][col] = 0; // Remove stone from the board
                return;
            }
        } else {
            System.out.println("Not the right player's turn or invalid position.");
            return;
        }

        // Validate the move before executing it
        if (!isMoveValid(row, col, newRow, newCol, startTray, playerNumber)) {
            System.out.println("Invalid move! Please try again.");
            return;
        }

        // Execute move
        System.out.println("Player " + (playerNumber == 1 ? "White" : "Black") + " moved stone from row "+ (row+1)+ ",  column "+(col+1) +" to row " + (newRow+1) + ", column " + (newCol+1));
        board[row][col] = 0; // Clear old position
        board[newRow][newCol] = playerNumber == 1 ? 1 : 2; // Place new position
    }






}



