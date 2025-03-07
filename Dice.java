public class Dice {
    private int EyesDye1;
    private int EyesDye2;

    public int[] getEyes() {
        int first = Math.max(EyesDye1, EyesDye2);
        int second = Math.min(EyesDye1, EyesDye2);

        return new int[]{first, second};
    }


    public void rollDice(){
        EyesDye1 = 4;//(int) (Math.random() * 6) + 1;
        EyesDye2 = 6;//(int) (Math.random() * 6) + 1;
    }

    public void displayDices(){
        System.out.println("Dice Roll: " + EyesDye1 + " & " + EyesDye2);
    }

}
