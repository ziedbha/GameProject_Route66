
//Score object. It stores the name of the player + his/her score. It is also comparable (needed to sort list of scores)
public class Score implements Comparable<Score> {
    private int sc; //score of player
    private String name; //players name
    
    //Constructor
    public Score(int sc, String name) {
        this.sc = sc;
        this.name = name;
    }
    
    //Getter methods
    public int getScore() {
        return sc;
    }
    
    public String getName() {
        return this.name;
    }
    
    //compareTo method. Needed because this class extends Comparable
    @Override
    public int compareTo(Score o) {
       return -(this.getScore() - o.getScore());
    }
}
