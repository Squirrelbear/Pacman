import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * GamePanel class:
 * Creates the panel to contain the map and issues updates as required.
 * Handles the timer for time between updates.
 */
public class GamePanel extends JPanel implements ActionListener {
    /**
     * Size of the grid for each individual cell.
     */
    public static final int CELL_DIM = 20;
    /**
     * Timer used for updating and drawing at set intervals.
     */
    private Timer timer;
    /**
     * Reference to the map.
     */
    private Map map;
    /**
     * Reference to the player's object.
     */
    private Pacman pacman;
    /**
     * The time between updates for the timer.
     */
    public static final int TIME_BETWEEN_UPDATES = 40;

    /**
     * Creates a map, with sufficient panel space to draw it and configures the
     * timer to start ticking for updates and repainting.
     */
    public GamePanel() {
        // Note that this currently assumes the map size.
        // You could create the map first and then request the dimensions from it.
        setPreferredSize(new Dimension(28*CELL_DIM, 31*CELL_DIM));
        setBackground(Color.BLACK);
        timer = new Timer(TIME_BETWEEN_UPDATES,this);
        timer.setRepeats(true);
        map = new Map();
        pacman = map.getPacman();

        timer.start();
    }

    /**
     * Draws the map, the score, and if necessary a game over or game won message.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        map.paint(g);
        drawScore(g);
        if(map.getMapState() == Map.MapState.GameOver) {
            drawGameEndMessage(g, "GAME OVER!", Color.RED);
        } else if(map.getMapState() == Map.MapState.GameWon) {
            drawGameEndMessage(g, "GAME WON!", new Color(21, 123, 21));
        }
    }

    /**
     * Updates the map including any necessary objects on it.
     * Then repaints the panel.
     *
     * @param e Not used.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        map.update();
        repaint();
    }

    /**
     * Handles input from the keyboard. Escape quits the game, R restarts.
     * Otherwise the input is passed on to pacman to let it move if possible.
     *
     * @param keyCode The key that was pressed.
     */
    public void handleInput(int keyCode) {
        if(keyCode == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        } else if(keyCode == KeyEvent.VK_R) {
            map.restart();
            pacman = map.getPacman();
        } else {
            pacman.moveIfCan(keyCode);
        }
    }

    /**
     * Draws the score centered at the top of the screen with a rectangle behind it.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    private void drawScore(Graphics g) {
        String score = String.valueOf(map.getScore());
        Font font = new Font("Arial", Font.BOLD, 25);
        g.setFont(font);
        int width = g.getFontMetrics().stringWidth(score);
        int widthMax = g.getFontMetrics().stringWidth("200");
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(14*CELL_DIM-widthMax/2,0,widthMax, 23);
        g.setColor(Color.BLACK);
        g.drawString(score, 14*CELL_DIM - width/2, 20);
    }

    /**
     * Centres the message in the panel with a pair of lines after for
     * the score and a message indicating R to restart. Also has a rectangle behind
     * to make it easier to read.
     *
     * @param g Reference to the Graphics object for rendering.
     * @param message The message to be rendered.
     * @param messageColour The colour to draw the specified message with.
     */
    private void drawGameEndMessage(Graphics g, String message, Color messageColour) {
        Font font = new Font("Arial", Font.BOLD, 25);
        g.setFont(font);
        int messageWidth = g.getFontMetrics().stringWidth(message);
        String scoreMessage = "Score: " + map.getScore();
        int scoreWidth = g.getFontMetrics().stringWidth(scoreMessage);
        String restartMessage = "Press R to Restart!";
        int restartWidth = g.getFontMetrics().stringWidth(restartMessage);
        int maxWidth = Math.max(Math.max(messageWidth,scoreWidth),restartWidth);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(getWidth()/2-maxWidth/2-5,getHeight()/2-65, maxWidth+10, 80);
        g.setColor(messageColour);
        g.drawString(message, getWidth()/2-messageWidth/2, getHeight()/2-40);
        g.setColor(Color.BLACK);
        g.drawString(scoreMessage, getWidth()/2-scoreWidth/2, getHeight()/2-15);
        g.drawString(restartMessage, getWidth()/2-restartWidth/2, getHeight()/2+10);
    }
}
