public class Piece {
    private int x;
    private int y;

    private int color_index;

    public void setPosition(int x_val,int y_val){
        this.x=x_val;
        this.y=y_val;
    }

    public int [] getPosition (){
        return new int[] {x,y};

    }



}
