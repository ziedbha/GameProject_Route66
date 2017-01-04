import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//a car object is just a GameObj with extra state (lane, bound, image associated with it)
/*it also has an extra method, outOfBounds, which tells if the car reached the end of the screen (useful for computer 
controlled cars)*/

public class Car extends GameObj {
    private String img_file; //image name 
    private int bound; //vertical bound
    private int lane; //lane it travels through
    private BufferedImage img;
    
    public static final int INCR = 15; //constant used for the jump animation
    
    //constructor
    public Car(int v, int x, int y, int size_x, int size_y, 
                                    int courtWidth, int courtHeight, String img_name, String name) {
        super(v, x, y, 
                size_x, size_y, courtHeight, name);
        
        this.img_file = img_name;
        this.bound = courtHeight;
        
        switch (x) {
        case 100: this.lane = 0; //left lane
                  break;
        case 250: this.lane = 1; //middle lane
                  break;
        case 400: this.lane = 2; //right lane
                  break;
        } //lane depends on x position
        
        try {
            if (img == null) {
                img = ImageIO.read(new File(img_file));
            }
        } catch (IOException e) {
            System.out.println("Internal Error:" + e.getMessage());
        }
    }
    
    
    //Getter/Setter method for lane
    public int getLane() {
        return this.lane;
    }
    
    public void setLane(int l) {
        this.lane = l;
    }
    
    //returns true when upperleft corner of car is out of the vertical bound (which is the courtHeight)
    public boolean outOfBounds() {
        return (this.getY() > bound);
    }
    
    /*----------- DRAW METHOD FOR CARS --------*/
    //draws car using image file
    @Override
    public void draw(Graphics g) {
        g.drawImage(img, this.getX(), this.getY(), this.getWidth(), this.getHeight(), null);
    }
}
