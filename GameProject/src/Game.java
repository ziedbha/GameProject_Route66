import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;


//Actual Game class. Creates frames, manages panels/statuses, manages high score IO, buttons...
public class Game implements Runnable {
     
    //Create a timer object for the Game
    public static final int INTERVAL = 35;
    Timer timer = new Timer(INTERVAL, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            tick();
        }
    });
    //Private fields that indicate if player is on the menu/playing the game, and other boolean values useful for IO
    private boolean onMenu; //is player on Menu screen?
    private boolean onGame; //is player playing the actual game?
    private boolean visited = false; //used for IO. Indicates whether the IO loop has been visited or not
    private boolean updated = false; //used for IO. Indicates whether the highscores have been updated or not
    
    /** JFRAMES */
    final JFrame frame = new JFrame("Route 66"); //Main game screen
    JFrame frame2 = new JFrame("High Scores!"); //High scores window
    
    /** JPANELS */
    final JPanel control_panel = new JPanel(); //Control bar. Has buttons for example
    final JPanel status_panel = new JPanel(); //Status bar. Displays status of program
    
    /** JLABELS */
    final JLabel status = new JLabel("LOADING..."); //bottom status
    final JLabel score = new JLabel("Score: " + 0); //score field
    final JLabel jump = new JLabel("Jump"); //jump status (on cooldown or not)
    final JLabel name = new JLabel(""); //name of player
    
    /** JBUTTONS */
    final JButton play = new JButton("Play Game"); //play game (from Menu screen)
    final JButton hi_scores = new JButton("High Scores"); //open high scores window (from Menu screen)
    final JButton reset = new JButton("Restart"); //restart game (from game screen)
    final JButton start_menu = new JButton("Start Menu"); //go back to start menu (from game screen)
    
    /** TEXTFIELD */
    final JTextField text = new JTextField("PLAYER", 5); //text box for player to enter their name
    
    /** Menu and Game Object */
    final GameCourt court = new GameCourt(status, score, jump); //has access to 3 jlabels
    final GameMenu menu = new GameMenu(status, text); //has access to 1 jlabel and 1 textfield
    
     //Run Method. Initializes most fields and sets up initial frame.
     public void run() {
        //initialize some fields
        onMenu = true;
        onGame = false;
        visited = false;   
        
        //start timer
        timer.start();
        
        //set location of window, add panels, add menu screen in a border layout
        frame.setLocation(400, 100);        
        frame.add(status_panel, BorderLayout.SOUTH);
        status_panel.add(status);
        frame.add(control_panel, BorderLayout.NORTH);     
        frame.add(menu, BorderLayout.CENTER);
        
        /** Adding action performed to each button (except high scores, which just opens up a new window) */
        // Play button
        play.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (onMenu){
                    onMenu = false;
                    onGame = true;
                    visited = false;
                    menu.exitMenu();
                    
                } 
            }
        });
        
        // Reset button
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (onGame){
                    court.reset();
                    visited = false;
                }
            }
        });
        // Start Menu button
        start_menu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (onGame) {
                    court.setGameOver();
                    onGame = false;
                    onMenu = true;
                    visited = false;
                    court.quit();
                    menu.goToMenu();
                    
                }
            }
        });
        
        
        /** Adding buttons to control panel (menu screen)*/
        control_panel.add(play);
        control_panel.add(hi_scores);
        
        /** Pack the high scores menu for later use */
        frame2.pack();
        
        /** Pack and get the main game window ready */
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        /** Start game */
        menu.goToMenu();
    }    
    
    //Tick method. Called each INTERVAL. Checks if game is over (does IO then), or if some buttons are pressed
    void tick() {
        /** ACTION 1: Gameover happened -> register new score and update high scores if needed */
        if ((court.isGameOver()) && (!visited)) {
             visited = true;
             updated = false;
             List<Score> scores = new ArrayList<Score>(); 
             try {
                 BufferedReader r = new BufferedReader(new FileReader("scores.txt")); 
                 String line = r.readLine();            
                 while (line != null) { //read a line, parse it, then create a score object accordingly, then store it
                     String[] split = line.split(" "); //splits line according to space                
                     String name = split[0];
                     int sc = Integer.parseInt(split[1]);
                     //cleans line into name and score
                     Score score_new = new Score(sc, name);
                     scores.add(score_new);
                     line = r.readLine();
                 } //read line in scores.txt and store the Score object in the scores list
                 r.close(); //close read file
                 String current_player = text.getText(); //get player name
                 String[] split = current_player.split(" ");
                 current_player = "";
                 for (int i = 0; i < split.length - 1; i++) {
                         current_player = current_player + split[i] + "&";
                 } 
                 current_player = current_player + split[split.length - 1]; //clean player name
                 int current_score_value = court.getScore(); //get player's score
                 Score current_score = new Score(current_score_value, current_player); //create current score object
                 scores.add(current_score); //store it
                 Collections.sort(scores); //sort the list
                 BufferedWriter w = new BufferedWriter(new FileWriter("scores.txt")); 
                 for (int i = 0; i < Math.min(10, scores.size()); i++) {
                        Score visiting = scores.get(i);
                        w.write(visiting.getName());
                        w.write(" ");
                        w.write(visiting.getScore() + "");
                        w.write("\n");
                  } //only write the 10 first scores
                 w.close(); //close file
              } catch (IOException e) {
                  System.out.println("File I/O error!");
              }
         }
         
         /** ACTION 2: If play button is pressed, reset the frame such that it displays the game court */
         if (play.getModel().isPressed()) {          
            try {
                Thread.sleep(250);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            } //add some delay so that player does not spam buttons
            
            control_panel.removeAll();
            control_panel.add(jump);
            control_panel.add(reset);
            control_panel.add(start_menu);
            control_panel.add(score);
            control_panel.add(name); //reset panel and add appropriate buttons
            
            frame.remove(menu); //remove the menu
            frame.add(court); //add the court 
            name.setText("|  " + text.getText());
            court.reset(); //start the game
            
            frame.setSize(court.getPreferredSize());
            frame.setLocation(600, 100); //resize and set the location of the frame
            
        }
        
         /** ACTION 3: If start menu pressed, go back to main menu by resetting the frame */
        if (start_menu.getModel().isPressed()) {            
            try {
                Thread.sleep(250);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            } //add some delay so that player does not spam buttons
            
            control_panel.removeAll();
            control_panel.add(play);
            control_panel.add(hi_scores); //reset panel and add appropriate buttons
            
            text.setText("PLAYER");
            
            frame.setLocation(400, 100);
            frame.remove(court);
            frame.add(menu);
            
            Dimension d = new Dimension(1200, 810);
            frame.setSize(d);
            
        }
        
        /** ACTION 4: If high scores button is pressed, pop the high scores window and update it if needed */
        if(hi_scores.getModel().isPressed() && !(frame2.isVisible())) {
             if (!updated) {
                frame2 = updateFrame(); // if was not updated previously, then update scores window
             }
            updated = true;
            frame2.pack(); //set up the window then display it
            frame2.setLocation(frame.getLocation());
            frame2.setSize(400, 200);
            frame2.setVisible(true); 
        }
    }

//Isolated method used to update the high scores window. Uses IO to read scores from scores.txt file
public JFrame updateFrame() {
    JLabel s1 = new JLabel("-----");
    JLabel s2 = new JLabel("-----");
    JLabel s3 = new JLabel("-----");
    JLabel s4 = new JLabel("-----");
    JLabel s5 = new JLabel("-----");
    JLabel s6 = new JLabel("-----");
    JLabel s7 = new JLabel("-----");
    JLabel s8 = new JLabel("-----");
    JLabel s9 = new JLabel("-----");
    JLabel s10 = new JLabel("-----");
    List<JLabel> labels = new ArrayList<JLabel>(); //store labels in a list
    labels.add(s1);
    labels.add(s2);
    labels.add(s3);
    labels.add(s4);
    labels.add(s5);
    labels.add(s6);
    labels.add(s7);
    labels.add(s8);
    labels.add(s9);
    labels.add(s10);
   
    try {
        BufferedReader r = new BufferedReader(new FileReader("scores.txt")); 
        String line = r.readLine();     
        Iterator i = labels.iterator();
        while (line != null) {
            JLabel current = (JLabel) i.next();
            current.setText(line); 
            line = r.readLine(); //edit labels with contents of the scores in the text file
        }  
        r.close();
    } catch (IOException e) {
        
    } 
     JFrame f = new JFrame("High Scores!");
     GridLayout l = new GridLayout (10,0);
     f.setLayout(l); //get the window set up by making the layout and adding the labels
     f.add(s1);
     f.add(s2);
     f.add(s3);
     f.add(s4);
     f.add(s5);
     f.add(s6);
     f.add(s7);
     f.add(s8);
     f.add(s9);
     f.add(s10);
     return f;
}

//Main method. Without this, game won't run!
public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game());
    }
}
