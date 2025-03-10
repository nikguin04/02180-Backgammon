public class Board {
    private int[][] board;

    // Constructor that initializes the board with the default values
    public Board() {
        this.board = new int[][] {
                {5, 0, 0, -3, 0,-5, 0, 0, 0, 0, 2},
                {-5, 0, 0, 3, 0, 5, 0, 0, 0, 0, -2},
        };
    }

    // Method to print the board based on the matrix



    public int[][] getMatrix() {
        return this.board;
    }


    public void translateBoard(int[][] board) {

        int[][] boardCopy = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(board[i], 0, boardCopy[i], 0, board[i].length);
        }
        System.out.println("In the following board black stones are shown as \"B\" and white stones as \"W\"");
        System.out.println("========================================");
        System.out.println("      1  2  3  4  5  |  6  7  8  9 10 11");
        System.out.println("+--------------------------------------+");

        for (int row = 0; row < boardCopy.length; row++) {  // Loop through all rows of the board
            for (int i = 0; i < 5; i++) {  // Loop 5 times before switching to the next row
                System.out.print(String.format("%2d", row + 1) + " | ");  // Print row index (1-based index)

                for (int col = 0; col < boardCopy[row].length; col++) {  // Loop through all columns in each row
                    if (col == 5) {
                        System.out.print(" | ");  // Print the vertical line separator after the 5th column
                    }

                    // Handle different cases based on the value
                    if (boardCopy[row][col] > 0) {
                        System.out.print(" W ");  // White player's piece
                        boardCopy[row][col]--;  // Decrease the value
                    } else if (boardCopy[row][col] < 0) {
                        System.out.print(" B ");  // Black player's piece
                        boardCopy[row][col]++;  // Increase the value
                    } else {
                        System.out.print(" . ");  // Empty space
                    }
                }
                System.out.println();  // Move to the next line for each repetition

            }

            if (row == 0) {
                System.out.println("   |------------------------------------");
            }

        }
        System.out.println("+--------------------------------------+");
        System.out.println("      1  2  3  4  5  |  6  7  8  9 10 11");
        System.out.println("========================================");

    }






    public boolean isMoveValid(int startRow, int startCol, int endRow, int endCol, StartTray startTray,int playerNumber) {

        // Check if the move is within the bounds of the board
        if (endRow < 0 || endRow >= board.length || endCol < 0 || endCol >= board[0].length) {
            System.out.println("Invalid move");
            return false;
        }

        System.out.println("Checking: board[" + endRow + "][" + endCol + "] = " + board[endRow][endCol]);
        if (board[endRow][endCol] == 5 || board[endRow][endCol] == -5) {
            System.out.println("Invalid move triggered!");
            return false;
        }


        if (startTray.getStones(playerNumber) != 0) {  // Corrected method call and comparison
            // The player is trying to move a piece on the board instead of placing one from the start tray
            if (board[startRow][startCol] != 0) {
                System.out.println("Invalid move");
                return false; // Invalid move: Player must first place a piece from the start tray
            }
        }

        // Get the value at the given spot
        int spotValue = board[startRow][startCol];

        // Determine the team and the opponent based on the value at the spot
        int player = (spotValue > 0) ? 1 : -1;  // Team 1 for positive, Team 2 for negative
        int opponent = -player;  // Flip the sign to get the opponent's team


        // Count the number of opponent pieces in the destination column (endY column)
        int opponentPieceCount = 0;

        if (board[endRow][endCol]==opponent){
            opponentPieceCount=board[endRow][endCol];
        }

        if (opponentPieceCount>1) {
            System.out.println("Invalid move!");
            return false;
        } else if (opponentPieceCount==1) {

            board[endRow][endCol]=0;
            System.out.println("Opponent put to Tray!");
            startTray.add(opponent); // Add the opponent's piece to the start tray


        }
        System.out.println("Debugging: endRow = " + endRow + ", endCol = " + endCol);

        if (playerNumber==1 && endCol>10 && endRow==1) {
            int positiveCount = 0; // Counter for positive stones in columns 0-4

            // Iterate only over columns 0-4 in row 0
            for (int col = 0; col <= 4; col++) {
                if (board[0][col] > 0) {
                    positiveCount++; // Count positive stones
                }
            }
            return (positiveCount == 15);

        }


        else if (playerNumber == 2 && endCol < 0 && endRow == 0) {
            int negativeCount = 0; // Counter for negative stones in columns 6-11

            // Iterate only over columns 6-11 in row 1
            for (int col = 6; col <= 11; col++) {
                if (board[1][col] < 0) { // Count negative stones
                    negativeCount++;
                }
            }

            return (negativeCount == 15); // Check if there are exactly 15 negative stones
        }



        return true;
    }



















    // Moves a piece on the board
    public void makeMove(int[][] board, int row, int col, int diceValue, int playerNumber, WinTray winTray, StartTray startTray) {
        // Determine movement direction
        int newCol = col;
        int newRow = row;

        // Move right for Player 1 (White)
        if (playerNumber == 1 && board[row][col] > 0) {
            newCol=newCol+diceValue;
            if (row==1 && newCol>10) {
                // Validate the move before making it
                if (!isMoveValid(row, col, row, newCol, startTray, playerNumber)) {
                    System.out.println("Invalid move! Please try again.");
                    return;
                }

                // Move the stone to the winning tray (increment winTray)
                winTray.upAmountStones(1);
                board[row][col]--;  // Remove the stone from the board
                System.out.println("Player 1 (White) reached the winning tray!");
                return;
            }

            else if (newCol >= 11 && row==0){
                    newCol %= 11;
                    newRow  ++;

            }

        }
        else if (playerNumber == 2 && board[row][col] < 0) {

            newCol=newCol-diceValue;
            if (row==0 && newCol < 0) {

                if (!isMoveValid(row, col, row, newCol, startTray, playerNumber)) {
                    System.out.println("Invalid move! Please try again.");
                    return;
                }

                // Move the stone to the winning tray (increment winTray)
                winTray.upAmountStones(-1);
                board[row][col]++;  // Remove the stone from the board
                System.out.println("Player 2 (Black) reached the winning tray!");
                return;
            }

            else if (newCol < 0 && row==1){

                    newCol = (newCol + 11) % 11;
                    newRow  --;

            }

        }



        else {
            System.out.println("Not the right player's turn or invalid position.");
            return;
        }

        // Validate the move before executing it
        if (!isMoveValid(row, col, newRow, newCol, startTray, playerNumber)) {
            System.out.println("Invalid move! Please try again.");
            return;
        }

        // Execute move
        System.out.println("Player " + (playerNumber == 1 ? "White" : "Black") + " moved stone from row "+ (row)+ ",  column "+(col) +" to row " + (newRow) + ", column " + (newCol));


        if (playerNumber==1){
            board[row][col]--;
            board[newRow][newCol]++;
        }
        else {
            board[row][col] ++;
            board[newRow][newCol]--;
       }
    }






}



