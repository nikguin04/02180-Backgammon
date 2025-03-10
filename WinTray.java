public class WinTray {
    private int PiecesPlayerWhite = 0;
    private int PiecesPlayerBlack = 0;

    // Method to increment the number of pieces for the player
    public void upAmountStones(int playerNumber) {
        if (playerNumber == 1) {
            this.PiecesPlayerWhite++;
        } else {
            this.PiecesPlayerBlack++;
        }
    }

    // Method to check if either player has won (reached 15 pieces)
    public boolean winCheck() {
        if (PiecesPlayerWhite == 15 || PiecesPlayerBlack == 15) {
            return true;
        } else {
            return false;
        }
    }

    // Method to get the current values of the pieces for both players
    public int[] getValues() {
        return new int[] { this.PiecesPlayerWhite, this.PiecesPlayerBlack };
    }
}
