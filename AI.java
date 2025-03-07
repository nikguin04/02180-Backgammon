public class AI extends Player {

    public AI(String color) {
        super(color, false);  // Always AI if this constructor is called
    }

    public int[] makeMove() {return new int[]{1,2};};
}
