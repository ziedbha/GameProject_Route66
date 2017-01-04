import java.awt.Graphics;

/* class that defines a game object, i.e a sprite, a background, anything that moves and does something related to the
game */
public abstract class GameObj implements Comparable <GameObj> {
    private int pos_x; //x coordinate of the object
    private int pos_y; //y coordinate of the object
    private int width; //width in pixels
    private int height; //heightin pixels
    private int v; //velocity in y-direction (since nothing moves in the x-direction in this game)
    private String name; //name field. Makes comparisons easier for collections
    
    //constructor
    public GameObj(int v, int pos_x, int pos_y, 
        int width, int height, int court_height, String name){
        this.v = v;
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        this.width = width;
        this.height = height;
        this.name = name;

    }
    
    /*------------ GETTER/SETTER METHODS ------------------ */
    public int getX() {
        return this.pos_x;
    }
    
    public int getY() {
        return this.pos_y;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getSpeed() {
        return this.v;
    }
    
    public void setSpeed(int v) {
        this.v = v;
    }
    
    public void setX(int x) {
        this.pos_x = x;
    }
    
    public void setY(int y) {
        this.pos_y = y;
    }
    
    public void setWidth(int w) {
        this.width = w;
    }
    
    public void setHeight(int h) {
        this.height = h;
    }
       
    
    /*------------- SPECIAL MEHTODS (COLLISION, DRAWING, MOVING... ---------- */
  //a move method used to translate the object
    public void move(){
        pos_y += v;
    }
    
    //collision detection
    public boolean collides(GameObj obj){
        return (pos_x + width >= obj.pos_x
                && pos_y + height >= obj.pos_y
                && obj.pos_x + obj.width >= pos_x 
                && obj.pos_y + obj.height >= pos_y);
    }
    
    //am abstract draw method. Each game object has its own drawing specifications
    public abstract void draw(Graphics g);
    
    //compareTo method needed to implement Collections
    @Override
    public int compareTo(GameObj obj) {
        return this.name.compareTo(obj.name);
    } 
    
    //Override hashCode and equals
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + pos_x;
        result = prime * result + pos_y;
        result = prime * result + v;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GameObj other = (GameObj) obj;
        if (pos_x != other.pos_x)
            return false;
        if (pos_y != other.pos_y)
            return false;
        if (v != other.v)
            return false;
        return true;
    }
    
    
}
