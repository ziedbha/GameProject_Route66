import java.util.*;


//a ComputerCar object is just a Car object with extra state denoting if it is on the screen and if its appearance
//should be delayed
/*it has extra methods that deal with sending it to the screen and one that deals with managing 
 * delays (making sure that there are no 3 adjacent cars blocking the player's way*/
public class ComputerCar extends Car {
    private boolean inPlay; //state that tells if the car is currently on the field or not
    private boolean toDelay; //state that tells if this car is being delayed or not
    private int delayDuration; //how many ticks used to delay this car
    
    int[] sending = {0, 1}; //array needed for randomizing car appearances
    
    //constructor. New state needed is the inPlay and toDelay booleans, in addition to a delayDuration
    public ComputerCar (int v, int x, int y, int size_x, int size_y, 
            int courtWidth, int courtHeight, String img_name, String name, int delay) {
        super(v, x, y, size_x, size_y, 
            courtWidth, courtHeight, img_name, name);
        this.inPlay = false;
        this.toDelay = false;
        this.delayDuration = delay;
    }
       
    //static method that generates a random number from an array
    public static int getRandomSend(int[] a) {
        int random = new Random().nextInt(a.length);
        return a[random];
    }
    
    
    //change lane given a new position x
    public void changeLane(int x) {
        this.setX(x);
        switch (x) {
        case 100: this.setLane(0); //left lane
                  break;
        case 250: this.setLane(1); //middle lane
                  break;
        case 400: this.setLane(2); //right lane
                  break;
        } //lane depends on x position
    }
    
    /* METHODS RELATED TO SENDING A CAR TO THE SCREEN */
    //asks if this car is currently on the screen or not
    public boolean inPlay() {
        return this.inPlay;
    }
    
    //get the x-coordinate when the car is on the screen. Returns -1 if the car is not on the screen
    public int getXInPlay() {
        if (this.inPlay()) {
            return this.getX();
        }
        return -1;
    }
    
    //get the y-coordinate when the car is on the screen. Returns -1 if the car is not on the screen
    public int getYInPlay() {
        if (this.inPlay()) {
            return this.getY();
        }
        return -1;
    }
    
    //returns the lane the car is currently on if it is on the screen. Returns -1 if the car is not on the screen
    public int getLaneInPlay() {
        if (this.inPlay()) {
            return this.getLane();
        }
        return -1;
    }
    
    //generates a number (0 or 1). If 1, send the car. If 0, do not send car
    public boolean shouldSend() {
        int n = getRandomSend(sending);
        if (n == 1) {
            return true;
        }
        return false;
    }
    
    //sends car in a random lane given a list of lanes available
    public void sendInPlay(List<Integer> positions) {
        int n = GameCourt.getRandom(positions);
        this.changeLane(n);
        this.inPlay = true;
    }
    
    //if the car crosses its bounds, return it back up with position 0 such that it does not appear on the screen
    public void getBack() {
        this.setY(-150);
        this.inPlay = false;
    }
    
    /* METHODS RELATED TO DELAYING THE APPEARANCE OF A CAR (USED TO AVOID HAVING 3 CARS BLOCKING THE PLAYER) */
    //starts the delay of this car
    public void delay() {
        this.toDelay = true;
    }
    
    //stops the delay of this car
    public void stopDelay() {
        this.toDelay = false;
    }
    
    //asks if this car is being delayed or not
    public boolean getDelay() {
        return this.toDelay;
    }
    
    //asks how many ticks used to delay this car
    public int getDelayduration() {
        return this.delayDuration;        
    }

}
