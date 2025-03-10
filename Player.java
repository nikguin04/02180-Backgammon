abstract class Player {
    int color_index;
    boolean isHuman;

    public Player(String color, boolean isHuman) {
        if (color.equals("White")) {
            this.color_index = 1;}
        else {
            this.color_index = -1;
        }
        this.isHuman = isHuman;
    }

    abstract int[] makeMove();


}