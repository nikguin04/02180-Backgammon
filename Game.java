import java.util.Scanner;

public class Game {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Welcome message with a newline for better formatting
        System.out.println("Welcome to our Game!");
        System.out.print("Please choose a color (White/Black): ");

        // Using nextLine() to get a full string input from the user
        String color1 = scanner.nextLine();

        // Ensure the input is valid
        while (!color1.equals("White") && !color1.equals("Black")) {
            System.out.println("Invalid color. Please choose a color (White/Black).");
            color1 = scanner.nextLine();
        }

        // Create the player object if the color is valid
        Player player1 = new Human(color1);
        System.out.println("Player created with color: " + color1);

        // Determine the color for the second player
        String color2 = color1.equals("White") ? "Black" : "White";

        System.out.print("Do you wish to play against AI? (Yes/No): ");
        String answer = scanner.nextLine();

        // Ensure the input is valid
        while (!answer.equals("Yes") && !answer.equals("No")) {
            System.out.println("Invalid answer. Do you wish to play against AI? (Yes/No)");
            answer = scanner.nextLine();
        }

        // Create the second player based on the user's response
        Player player2;
        if (answer.equals("Yes")) {
            player2 = new AI(color2);
            System.out.println("AI Player created with color: " + color2);
        } else {
            player2 = new Human(color2);
            System.out.println("Player created with color: " + color2);
        }

        Board board= new Board();
        Dice dice=new Dice();
        WinTray winTray=new WinTray();
        StartTray startTray= new StartTray();

        System.out.println("Your turn White!");
        // board.printBoard(board.getPrintMatrix());







        int currentPlayer = 1;  // 1 for White, -1 for Black

        while (!winTray.winCheck()) {
            dice.rollDice();
            dice.displayDices();
            int[] dices = dice.getEyes();
            board.translateBoard(board.getMatrix());

            int row, col;

            // Loop until the player selects a valid piece
            while (true) {
                System.out.print("Which stone should be moved with value " + dices[0] + "\n");
                System.out.print("Enter row: ");
                row = scanner.nextInt()-1;
                System.out.print("Enter column: ");
                col = scanner.nextInt()-1;

                // Check if move is valid
                if (board.isMoveValid(row, col, row, col + dices[0], startTray, currentPlayer)) {
                    break;  // If valid, break out of the loop
                }
                System.out.println("Invalid move! Try again.");
            }

            board.makeMove(board.getMatrix(), row, col, dices[0], currentPlayer, winTray, startTray);
            board.translateBoard(board.getMatrix());

            // Repeat for the second die
            int row2, col2;
            while (true) {
                System.out.print("Which stone should be moved with value " + dices[1] + "\n");
                System.out.print("Enter row: ");
                row2 = scanner.nextInt()-1;
                System.out.print("Enter column: ");
                col2 = scanner.nextInt()-1;

                // Check if move is valid
                if (board.isMoveValid(row2, col2, row2, col2 + dices[0], startTray, currentPlayer)) {
                    break;  // If valid, break out of the loop
                }
                System.out.println("Invalid move! Try again.");
            }

            board.makeMove(board.getMatrix(), row2, col2, dices[1], currentPlayer, winTray,startTray);

            if (winTray.winCheck()) {
                System.out.println("Player " + (currentPlayer == 1 ? "White" : "Black") + " wins!");
                break;
            }

            currentPlayer = (currentPlayer == 1) ? 2 : 1;
            System.out.println("Player " + (currentPlayer == 1 ? "White" : "Black") + ", it's your turn.");
            System.out.println("The winning tray contains the following stones: White " + winTray.getValues()[0] + " Black " + winTray.getValues()[1]);
        }


        scanner.close();
    }









}