import java.util.Scanner;

public class Human extends Player {
    public Human(String color) {
        super(color, true);  // Always human if this constructor is called
    }

    @Override
    public int[] makeMove() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the row of the stone you want to move: ");
        int fromRow = scanner.nextInt();
        System.out.print("Enter the column of the stone you want to move: ");
        int fromCol = scanner.nextInt();


        // Return both the from and to positions
        return new int[]{fromRow, fromCol};
    }
}





