//A UserCar, is a Car with extra methods that handles the jumping mechanic of the game (animation for example)
public class UserCar extends Car {
    private boolean jump; //is the user's car in jumping mode?
    private int jumpLimit; //how many ticks until jump animation is done
    
    public UserCar(int v, int x, int y, int size_x, int size_y, 
            int courtWidth, int courtHeight, String img_name, String name) {
        super(v, x, y, size_x, size_y, 
                courtWidth, courtHeight, img_name, name);
        this.jump = false;
        this.jumpLimit = 20;
    }

    
  /*---- JUMPING RELATED METHODS ----*/
    
   public void startJump() {
       this.jump = true;
   }
   
   public void endJump() {
       this.jump = false;
   }  
   
   public boolean isJumping() {
       return this.jump;
   }
   
   public int getJumpLimit() {
       return this.jumpLimit;
   }
    
 /*---------- JUMP ANIMATION METHOD ----------- */
    
    //increases the size of the car when jumping up
    public void increaseSize() {
        this.setWidth(this.getWidth() + INCR);
        this.setHeight(this.getHeight() + INCR);
        this.setX(this.getX() - (INCR/2));
        this.setY(this.getY() - (INCR/2));
    }
    
    //reverts the size of the car back after a jump
    public void decreaseSize() {
        this.setWidth(this.getWidth() - INCR);
        this.setHeight(this.getHeight() - INCR);
        this.setX(this.getX() + (INCR/2));
        this.setY(this.getY() + (INCR/2));
    }
}
