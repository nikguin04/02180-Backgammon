public class WinTray {
    private int PicesPlayer1=0;
    private int PicesPlayer2=0;

    public void upAmountStones(int playerNumber){
        if (playerNumber==1){PicesPlayer1++;}
        else {PicesPlayer2++;}

        ;
    }

    public boolean winCheck() {
        if (PicesPlayer1==15 || PicesPlayer2==15){return true;}
        else {return false;}
    }
}
