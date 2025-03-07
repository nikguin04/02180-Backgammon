public class StartTray {

    private int PicesPlayer1=0;
    private int PicesPlayer2=0;

    public void add(int playerNumber){
        if (playerNumber==1){this.PicesPlayer1++;}
        else {this.PicesPlayer2++;}

        ;
    }

    public int getStones(int playerNumber){
        if (playerNumber==1){
            return PicesPlayer1;
        }
        else {return PicesPlayer2;}
    }




}
