//When an ability (like jumping) is on "cooldown", it cannot be used. Once the cooldown disappears, the ability can
//be used. It is basically a timer that manages and ability (usually a boolean)

/** THIS ISOLATED PART OF THE GAMECOURT WAS TESTED IN THE CooldownTest.java JUnit test cases */
public class Cooldown {
    private boolean started; //did the cooldown start?
    private boolean ended; //did the cooldown end?
    private int duration; //duration of cooldown in ticks
    private int counter; //counts the number of ticks done since the cooldown started
        
    //constructor
    public Cooldown(int duration) throws IllegalArgumentException {
        this.started = false;
        this.ended = true;
        this.counter = 0;
        if (duration <= 0) {
           throw new IllegalArgumentException();
        } else {
            this.duration = duration;
        }
    }
    
    //start the cooldown
    public void startCooldown() {
        this.started = true;
        this.ended = false;
    }
    
    //if cooldown has started, then continue incrementing it
    public void continueCooldown() {     
        if (this.started) {
            this.counter += 1;
            if (this.counter == this.duration) {
                this.started = false;
                this.counter = 0;
                this.ended = true;
            }
        }
    }
    
    //resets cooldown fields to initial values for another use
    public void resetCooldown() {
        this.started = false;
        this.counter = 0;
        this.ended = true;
    }
    
    //returns true when cooldown has ended
    public boolean cooldownEnded() {
        return this.ended;
    }
    
}
