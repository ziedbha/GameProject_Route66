import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//an image can have velocity so it extends GameObj
public class Image extends GameObj {
    //constants: image name, sizes, x position (never changes), velocity (never changes)
    public static final int V = 20;
    
    //private img state
    private BufferedImage img;
    
    /*constructor. Note that y coordinate is variable because will need to have more than one background to create
    infinite background effect */
    public Image(int x, int y, int sizex, int sizey, int courtWidth, int courtHeight, String img_file, String name) {
        super(V, x, y, 
                sizex, sizey, courtHeight, name);
        try {
            if (img == null) {
                img = ImageIO.read(new File(img_file));
            }
        } catch (IOException e) {
            System.out.println("Internal Error:" + e.getMessage());
        }
    }
    
    //draws background similar to car except at constant x position (and sizes)
    public void draw(Graphics g) {
        g.drawImage(img, this.getX(), this.getY(), this.getWidth(), this.getHeight(), null);
    }

}