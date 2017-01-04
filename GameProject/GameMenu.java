
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//A GameMenu object is the first thing the player sees on the screen. It handles entering the menu, exiting it,
//displaying instructions...
@SuppressWarnings("serial")
public class GameMenu extends JPanel {
    //PRIVATE FIELDS
    private Image menu; //menu image. Has instructions on it
    private boolean onMenu;
    private JLabel status;
    private JTextField text; //text entered by player Player name)

    //MENU CONSTANTS
    public static final int MENU_WIDTH = 1200;
    public static final int MENU_HEIGHT = 700;
    public static final String MENU_IMG = "menu.png";
    public static final String BLK_IMG = "black.png";
    public static final int INTERVAL = 35;
    
    //TIMER THAT HANDLES REPAINT METHOD
    Timer timer = new Timer(INTERVAL, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            tick();
        }
    });
    
    //Constructor
    public GameMenu(JLabel status, JTextField text) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
        this.status = status;
        this.text = text;
        final JLabel name = new JLabel("Enter your name here: ");
        timer.start(); 
        setFocusable(true); //focus controls on menu
        this.add(name);
        this.add(text);
        menu = new Image(0, 0, MENU_WIDTH, MENU_HEIGHT, MENU_WIDTH, MENU_HEIGHT, MENU_IMG, "menu" );
    }
    
    
    /** METHODS FOR INTERACTING WITH GAME CLASS */
    //goes to menu screen
    public void goToMenu() {
        this.onMenu = true;
        timer.start();
        status.setText("Welcome to ROUTE 66! By Ziad Ben Hadj-Alouane");       
        requestFocusInWindow();
    }
    
    //exits menu screen
    public void exitMenu() {
        this.onMenu = false;
    }
    
    /** TICK METHODS. Handles repainting the menu each interval */
    void tick() {
       if (onMenu) {
           repaint();
       } else {
           timer.stop();
       }
    }
    
    //What the menu paints: the menu image 
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        menu.draw(g);
    }
    
    //Preferred size of menu screen
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(MENU_WIDTH, MENU_HEIGHT);
    }
}
