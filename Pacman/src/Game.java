import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * Game class:
 * Creates the entry point for the game by setting up the frame.
 */
class Game implements KeyListener {
    /**
     * Entry point to create the game.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        Game game = new Game();
    }

    /**
     * Reference to the GamePanel for passing key events.
     */
    private GamePanel gamePanel;

    /**
     * Creates the JFrame and adds a GamePanel to it.
     */
    public Game() {
        JFrame frame = new JFrame("Pacman");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.addKeyListener(this);

        gamePanel = new GamePanel();
        frame.getContentPane().add(gamePanel);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Passes the key code on to the game panel to allow it to handle what should happen.
     *
     * @param e Information about the key event that occurred.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        gamePanel.handleInput(e.getKeyCode());
    }

    /**
     * Not used.
     *
     * @param e Not used.
     */
    @Override
    public void keyReleased(KeyEvent e) {}
    /**
     * Not used.
     *
     * @param e Not used.
     */
    @Override
    public void keyTyped(KeyEvent e) {}
}
