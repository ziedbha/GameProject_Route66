import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;


//A GameCourt object contains all the logic of the actual game: how cars move, how the background moves,
//how the player controls their car, game start and end conditions, score display etc...
@SuppressWarnings("serial")
public class GameCourt extends JPanel {
    /** PRIVATE STATE */
    //private state includes all game objects and status of game
    private UserCar user; // The user's car, keyboard controls (this is the player)
    
    /* set of computer cars. Convenient since lots of operations of these computer controlled cars are analogous */
    private Set<ComputerCar> computerCars;  //set of Computer Controlled Cars, useful for doing bulk operations
    private ComputerCar computer1; //3 Computer Controlled cars, each with different sprite and speed
    private ComputerCar computer2;
    private ComputerCar computer3;
    private List<Integer> non_occup; //set of lanes occupied by computer cars currently
    private int counter; //counter that dictates rate of car appearance
    
    private Set<Image> bgs; //set of backgrounds, useful for doing bulk operations
    private Image bg1; //background 1
    private Image bg2; //background 2 (essentially the same as bg1 except at different position y)
    private Image one; //game startup images
    private Image two;
    private Image three;
    private Image go;
    private Image gameover; //gameover image
    private Image boom; //explosion image
    
    //private boolean jump; 
    private int jumpCounter;
    private boolean allowJump;
    private boolean justEnded = false; // indicates that cooldown just ended

    //fields all used to make sure 3 cars cannot block the player's way when they are close enough
    private boolean allthree; //are all three cars present on the screen?
    private boolean projected; //did we make projections on their future positions?
    private boolean delayed; //is the game in car-delay mode (delaying a car to allow player to dodge)
    private int delayLimit; //for how many ticks should the delay go on?
    private int delayCounter; //how many ticks passed since delay started
    private int oldSpeed; //car's initial speed right before the delay happens
    
    //status of the game
    private boolean playing = false; // whether the game is running
    private boolean lost = false;
    private JLabel status; // Current status text (i.e. Running...)
    private JLabel score;
    private JLabel jump_lbl;
    
    //Game starting state
    private boolean starting = true;
     
    /** GAME CONSTANTS */
    //court sizes
    public static final int COURT_WIDTH = 600;
    public static final int COURT_HEIGHT = 800;
    // car constants (sizes)
    public static final int SIZEX_CAR = 100;
    public static final int SIZEY_CAR= 150;
    //user car initial state (x and y coordinates), 0 speed, img name
    public static final int USER_X = 250;
    public static final int USER_Y = 630;
    public static final int USER_V = 0;
    public static final String USER_IMG = "user.png";
    //initial coordinate for computer cars that are not sent to play
    public static final int C_X = 0;
    public static final int C_Y = -SIZEY_CAR;
    //computer car constants (speeds): 
    public static final int C1_V = 40; //slowest car is number 1
    public static final int C2_V = 45;
    public static final int C3_V = 50; //fastest car is number 3
    public static final String C1_IMG = "computer1.png";
    public static final String C2_IMG = "computer2.png";
    public static final String C3_IMG = "computer3.png";
    //background image constants (initial y-coordinates, and image name)
    public static final int INITX = 0;
    public static final int INITY_B1 = -400; 
    public static final int INITY_B2 = -1600; 
    public static final int SIZEX_BG = 600;
    public static final int SIZEY_BG = 1200;
    public static final String BG_IMG = "bg.png";
    //1 2 3 Go! image
    public static final String ONE_IMG = "one.png";
    public static final String TWO_IMG = "two.png";
    public static final String THREE_IMG = "three.png";
    public static final String GO_IMG = "go.png";
    //gameover constants
    public static final int SIZEX_G = 400;
    public static final int SIZEY_G = 100;
    public static final String GG_IMG = "gg.png";
    //explosion img name
    public static final int SIZEX_B = 300;
    public static final int SIZEY_B = 300;
    public static final String BOOM_IMG = "boom.png";
    //score constants
    public static final int SX = 300;
    public static final int SY = 300;
    //lane width used for keyboard controls
    public static final int LANE_WIDTH = 150;
    // Update interval for timer, in milliseconds
    public static final int INTERVAL = 35;
    
    /** TIMER object used to perform actions each tick and to stop/start game */
    Timer timer = new Timer(INTERVAL, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            tick();
        }
    });
    
    /** COOLDOWN object used to allow/disallow jumping (after a jump, player has to wait a certain amount of time before
     * they can jump again
     */
    Cooldown jumpCool = new Cooldown(50);
    
    
    /** Making a GameCourt: setting timer, setting keyboard controls, setting status */
    public GameCourt(JLabel status, JLabel score, JLabel jump) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));      
        timer.start(); 
        setFocusable(true); //focus controls on court
        //define keyboard controls: Move left/right and jump
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (((e.getKeyCode() == KeyEvent.VK_LEFT) || (e.getKeyCode() == KeyEvent.VK_A)) && (!starting)) {
                    if (user.getLane() != 0) {
                        user.setX(user.getX() - LANE_WIDTH);
                        user.setLane(user.getLane() - 1);
                    }
                }
                else if (((e.getKeyCode() == KeyEvent.VK_RIGHT) || (e.getKeyCode() == KeyEvent.VK_D)) && (!starting)) {
                        if (user.getLane() != 2) {
                        user.setX(user.getX() + LANE_WIDTH);
                        user.setLane(user.getLane() + 1);
                        } 
                } else if ((e.getKeyCode() == KeyEvent.VK_SPACE) && (allowJump) && (!starting)) {
                        user.startJump();
                        }
            }
        });
        this.status = status;
        this.score = score;
        
        this.jump_lbl = jump; //set up the jump status indicator
        final Border blackBorder = BorderFactory.createLineBorder(Color.BLACK, 3);
        final Border paddingBorder = BorderFactory.createEmptyBorder(2,5,2,5);
        final CompoundBorder border = new CompoundBorder(
                blackBorder, 
                paddingBorder);
        jump.setOpaque(true);
        jump.setBackground(Color.GREEN);
        jump.setBorder(border);
 
    }

    /** Static method to generate a random number from a list of numbers */       
    public static int getRandom(List<Integer> a) {
        int random = new Random().nextInt(a.size());
        return a.get(random);
    }
    
    /** Method used to project the three car's future positions */
    public int[] projectFuture() {
        int cp1 = computer1.getY();
        int cp2 = computer2.getY();
        int cp3 = computer3.getY();
        
        int d1 = COURT_HEIGHT - cp1 - (SIZEX_CAR + 50);
        int d2 = COURT_HEIGHT - cp2 - (SIZEX_CAR + 50);
        int d3 = COURT_HEIGHT - cp3 - (SIZEX_CAR + 50);
        
        int t1 = d1/(computer1.getSpeed());
        int t2 = d2/(computer2.getSpeed());
        int t3 = d3/(computer3.getSpeed());
        
        int tick1 = t1/INTERVAL;
        int tick2 = t2/INTERVAL;
        int tick3 = t3/INTERVAL;
        
        int[] r = {tick1, tick2, tick3};
        
        return r;
     }
    
    /** Method that, given an interval of vertical positions, tells if it is possible to dodge three incoming cars */
    public boolean possibleToWin(int[] projections) {
        int l1 = computer1.getLane();
        int l2 = computer2.getLane();
        int l3 = computer3.getLane();
        
        int[] vdistances = new int[2];
        int count = 0;
        if (Math.abs(l1 - l2) == 1) {
            vdistances[0] = Math.abs(projections[0] - projections[1]);
            count += 1;
        }
        
        if (Math.abs(l1 - l3) == 1) {
            if (count == 0) {
            vdistances[0] = Math.abs(projections[0] - projections[2]);
            } else {
                vdistances[1] = Math.abs(projections[0] - projections[2]);
            }
        }
        
        if (Math.abs(l2 - l3) == 1) {
            if (count == 0) {
            vdistances[0] = Math.abs(projections[1] - projections[2]);
            } else {
                vdistances[1] = Math.abs(projections[1] - projections[2]);
            }
        }
        
        for (int i = 0; i < 2; i++) {
            if (vdistances[i] >= 200) {
                return true;
            }
        }
        return false;
    }
    
    
    /** Reset method. Initializes the state of everything */
    public void reset() {
        counter = 0;
        user = new UserCar(USER_V, USER_X, USER_Y, SIZEX_CAR, SIZEY_CAR, 
                                                    COURT_WIDTH, COURT_HEIGHT, USER_IMG, "U");
              
        computerCars = new TreeSet<ComputerCar>();
            computer1 = new ComputerCar(C1_V, C_X, C_Y, SIZEX_CAR, SIZEY_CAR, 
                    COURT_WIDTH, COURT_HEIGHT, C1_IMG, "C1", 10);
            computer2 = new ComputerCar(C2_V, C_X, C_Y, SIZEX_CAR, SIZEY_CAR, 
                    COURT_WIDTH, COURT_HEIGHT, C2_IMG, "C2", 30);
            computer3 = new ComputerCar(C3_V, C_X, C_Y, SIZEX_CAR, SIZEY_CAR, 
                    COURT_WIDTH, COURT_HEIGHT, C3_IMG, "C3", 35);
                computerCars.add(computer1);
                computerCars.add(computer2);
                computerCars.add(computer3);
                
        non_occup = new ArrayList<Integer>();
        non_occup.add(100);
        non_occup.add(250);
        non_occup.add(400);
         
        bgs = new TreeSet<Image>();
            bg1 = new Image(INITX, INITY_B1, SIZEX_BG, SIZEY_BG, COURT_WIDTH, COURT_HEIGHT, BG_IMG, "BG1");
            bg2 = new Image(INITX, INITY_B2, SIZEX_BG, SIZEY_BG, COURT_WIDTH, COURT_HEIGHT, BG_IMG, "BG2");
                bgs.add(bg1);
                bgs.add(bg2);
        
        one = new Image(COURT_WIDTH/2 - SIZEX_G/2 + 50, COURT_HEIGHT/2 - SIZEX_G/2, 
                                        SIZEX_B, SIZEY_B, COURT_WIDTH, COURT_HEIGHT, ONE_IMG, "1" );
        two = new Image(COURT_WIDTH/2 - SIZEX_G/2 + 50, COURT_HEIGHT/2 - SIZEX_G/2,
                                        SIZEX_B, SIZEY_B, COURT_WIDTH, COURT_HEIGHT, TWO_IMG, "2" );
        three = new Image(COURT_WIDTH/2 - SIZEX_G/2 + 50, COURT_HEIGHT/2 - SIZEX_G/2,
                                        SIZEX_B, SIZEY_B, COURT_WIDTH, COURT_HEIGHT, THREE_IMG, "3" );
        go = new Image(COURT_WIDTH/2 - SIZEX_G/2 + 50, COURT_HEIGHT/2 - SIZEX_G/2, 
                                            SIZEX_B, SIZEY_B, COURT_WIDTH, COURT_HEIGHT, GO_IMG, "GO" );
        gameover = new Image(COURT_WIDTH/2 - SIZEX_G/2, COURT_HEIGHT/2 - 20, 
                                    SIZEX_G, SIZEY_G, COURT_WIDTH, COURT_HEIGHT, GG_IMG, "GG" );
        boom = new Image(0, 0, SIZEX_B, SIZEY_B, COURT_WIDTH, COURT_HEIGHT, BOOM_IMG, "DED" );
                      
        jumpCounter = 0;
        allowJump = true;
        jumpCool.resetCooldown();
            
        allthree = false;
        delayed = false;
        delayLimit = 0;
        delayCounter = 0;
        oldSpeed = 0;
        projected = false;
        
        playing = true;
        lost = false;
        starting = true;
        this.score.setText("Score: 0");
        timer.start();
        status.setText("Running...");       
        requestFocusInWindow();
    }
    
    /** USEFUL METHODS USED TO INTERACT WITH GAME CLASS */
    //quit the court
    public void quit() {
        timer.stop();
        playing = false;
        this.score.setText("Score: 0");
    }
    //get current score
    public int getScore() {
         return counter*5;
    }
    //says if court is in gameover state or not
    public boolean isGameOver() {
        return lost;
    }
    //puts game in gameover state
    public void setGameOver() {
        this.lost = false;
    }
    
    /** Tick method. Has everything done during each INTERVAL (moving objects, checking for collision, etc...) */
    void tick() {
        if (playing) {
            counter +=1;
            if (!starting) {
                this.score.setText("Score: " + counter*5);
                //----- LOGIC #1: JUMPING MECHANIC --------//            
                //if player is in jumping mode, then modify car size using appropriate methods.
                //else start the cooldown for the jumping mechanic
                
                if ((user.isJumping())) {
                    this.jump_lbl.setBackground(Color.RED);
                    
                    allowJump = false;
                    jumpCounter += 1;  
                    if (jumpCounter <= (user.getJumpLimit()/2)) {
                        user.increaseSize();
                    } else if ((jumpCounter > (user.getJumpLimit()/2)) && (jumpCounter <= user.getJumpLimit())) {
                        user.decreaseSize();
                    } else if (jumpCounter > 20) {
                        user.endJump();
                        jumpCounter = 0;
                        justEnded = true;
                        jumpCool.startCooldown();
                      } 
                }
                //takes care of cooldown
                jumpCool.continueCooldown();
                if (jumpCool.cooldownEnded() && justEnded) {
                    justEnded = false;
                    allowJump = true;
                    this.jump_lbl.setBackground(Color.GREEN);
                }
                
                //------LOGIC #2: SENDING AND MOVING COMPUTER-CONTROLLED CARS -----//
                //move computer cars if they are in play
                for (ComputerCar c: computerCars) {                
                    if (c.inPlay()) {
                       if (c.getY() >= 700) {
                            c.setSpeed(150);
                        }
                        c.move();
                    //else decide if the car should be sent. If so, make sure to send it in a lane not already occupied
                    } else if ((c.shouldSend()) && ((counter % 2 == 0))) {
                        c.sendInPlay(non_occup);
                        int l = c.getXInPlay();
                        int i = non_occup.indexOf(l);
                        non_occup.remove(i);       
                      } 
                 }
                 
                //-------LOGIC #3: MAKING SURE THAT IF THERE ARE 3 CARS ON THE SCREEN, PLAYER HAS ENOUGH SPACE TO DODGE-//
                //STEP 1: Are there 3 cars on the screen?
                allthree = true;
                for (ComputerCar c: computerCars) {
                    if (!c.inPlay()) {
                        allthree = false;
                        break;                    
                    }
                }
                //STEP 2: If there are 3 cars on the screen, then project their future position and decide if player can 
                //dodge or not. If player cannot dodge, start delay methods of the car farthest to the back
                if ((allthree) && (!projected)) { //3 cars on screen and projections have not been done
                    projected = true;
                    int [] projections = new int[3];
                    projections = projectFuture(); //get future positions
                    int index = 0;
                    if (!possibleToWin(projections)) { //if not possible to dodge in the future, then pick
                        //which car to delay
                        int min = 1000;
                        int[] positions = {computer1.getY(), computer2.getY(), computer3.getY()};
                        for (int i = 0; i < 3; i++) {
                           if (positions[i] < min) {
                               min = positions[i];
                               index = i;
                           }
                        } 
                    }
                    //the index given by the previous loop denotes which car to delay
                    switch (index) {
                        case 0: computer1.delay(); //car in delay mode
                                oldSpeed = computer1.getSpeed(); //save car's current speed
                                computer1.setSpeed(5); //lower its speed
                                delayLimit = computer1.getDelayduration(); //how long should the car be delayed
                                delayed = true; //state of game is in delay mode
                                  break;
                        case 1: computer2.delay(); 
                                oldSpeed = computer2.getSpeed();
                                computer2.setSpeed(5);
                                delayLimit = computer2.getDelayduration();
                                delayed = true;
                                  break;
                        case 2: computer3.delay();
                                oldSpeed = computer3.getSpeed();
                                computer3.setSpeed(5);
                                delayLimit = computer3.getDelayduration();
                                delayed = true;
                                  break;
                    }
                }
                 
               //STEP 3: KEEP TRACK OF CAR DELAY. IF DELAY OVER THEN RESET SPEEDS        
               if (delayed) {
                   delayCounter +=1;
                   if ((delayCounter > delayLimit) || (!allthree)) {
                       for (ComputerCar c: computerCars) {
                           if (c.getDelay()) {
                               c.setSpeed(oldSpeed); //revert its speed back to what it was originally
                               c.stopDelay(); //put car in NON delay mode
                               break;
                           }
                       }
                       delayed = false; //game is not in delay mode anymore
                       delayLimit = 0; //reset delay variables
                       delayCounter = 0;
                       oldSpeed = 0;
                       projected = false; //make sure that we can make new projections as we go on  
                   }
               }
        
             //-------LOGIC #4: COLLISION DETECTION-------//
               //if player is not jumping (i.e immune to collision), then check for collision
               if (!user.isJumping()) {
                    for (ComputerCar c: computerCars) {
                        if (user.collides(c)) {
                            timer.stop(); //Game over if collided, stop timer
                            lost = true; //player has lost
                            status.setText("GAME OVER!"); //update status bar
                            break;
                        }
                    }
                }
            
             //-------LOGIC #5: INFINITE BACKGROUNDS -------//  
                //always move backgrounds
                for (Image b: bgs) {
                    b.move();
                }
              
              // Finally, repaint everything each tick    
            }
            repaint();
        }
    }
    
    
    //painting method
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //draw backgrounds such that they create an infinite background
        for (Image b: bgs) {
            if (!(b.getY() > COURT_HEIGHT)) {
                b.draw(g);
            } else {
                b.setY(-1200);
            }
        }
        
        //only draw computer cars if they are in play and if they are not out of bounds
        for (ComputerCar c: computerCars) {
            if ((!c.outOfBounds()) && (c.inPlay())) {
                c.draw(g);
            } else if (c.outOfBounds()) { //if out of bounds, then get them back, and reset their speeds
                non_occup.add(c.getXInPlay());
                c.getBack();
                if (c.equals(computer1)) {
                    c.setSpeed(C1_V);
                } else if (c.equals(computer2)) {
                    c.setSpeed(C2_V);
                } else {
                    c.setSpeed(C3_V);
                }
            }
        }
        
      //always draw user
      user.draw(g);
      //draw the startup images in a sequence
      if ((counter <= 20) && (starting)) {
          one.draw(g);
      }
      
      if ((counter > 20) && (counter <= 40) && (starting)) {
          two.draw(g);
      }
      
      if ((counter > 40) && (counter <= 60) && (starting)) {
          three.draw(g);
      }
      
      if((counter > 60) && (counter <= 80) && (starting)) {
          go.draw(g);    
      }
      
      if((starting) && (counter == 80)) {
          starting = false;
          counter = 0;
      }
      
      //handles drawing game end images
      if (lost == true) {
          gameover.draw(g); //draw Game Over image
          boom.setX(user.getX() - 100);
          boom.setY(user.getY() - 100);
          boom.draw(g);
          playing = false;
      }
    }

    //gets preferred size of game court. Used in Game class
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(COURT_WIDTH + 19, COURT_HEIGHT + 100);
    }
}

