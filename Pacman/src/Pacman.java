import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * Pacman class:
 * Represents the player's character that can move around the
 * map while animating the moving mouth.
 */
public class Pacman extends MapObject {
    /**
     * Current angle of the mouth. Animates between 90 and 27 degrees.
     */
    private int openAmount = 90;
    /**
     * Current facing direction.
     * 0=UP, 1=RIGHT, 2=DOWN, 3=LEFT
     */
    private int facing = 0;
    /**
     * True if an input can be applied to make Pacman move in a direction.
     * This becomes true when canMoveTimer triggers.
     */
    private boolean canMove;
    /**
     * Reference to the map to validate attempted movement.
     */
    private Map map;
    /**
     * Timer to delay time between movements.
     */
    private ActionTimer canMoveTimer;
    /**
     * Next move to apply during the next update.
     */
    private Position nextMove;

    /**
     * Initialises Pacman so it is ready for interaction.
     *
     * @param position The position to start Pacman at.
     * @param map Reference to the map for validating moves.
     */
    public Pacman(Position position, Map map) {
        super(position, ObjectType.Pacman, EnterType.Everyone);
        canMove = true;
        this.map = map;
        nextMove = Position.ZERO;
        canMoveTimer = new ActionTimer(160);
    }

    /**
     * Updates the Pacman by animating the mouth and
     * updating the timer for movement to delay between moves.
     */
    public void update() {
        // Animate the mouth angle
        openAmount -= 3;
        if(openAmount<30) {
            openAmount = 90;
        }

        // Update movement delay timer
        if(!canMove) {
            canMoveTimer.update();
            if(canMoveTimer.isTriggered()) {
                canMoveTimer.reset();
                canMove = true;
            }
        } else if(!nextMove.equals(Position.ZERO)) {
            position.add(nextMove);
            //System.out.println(position.x + " " + position.y);
            // Trigger any events based on the object that was entered.
            map.processEntering(this);
            nextMove = Position.ZERO;
            // Disable movement until the timer triggers again
            canMove = false;
        }
    }

    /**
     * Moves Pacman if it can by getting the unit vector in the direction.
     * The facing is always changed regardless of whether a move is completed.
     * If movement is valid based on the response from the map the move is
     * completed and any entry triggers are activated via the map.
     * Movement is then paused to start the movement delay timer.
     *
     * @param keyCode The key that was pressed.
     */
    public void moveIfCan(int keyCode) {
        // Waiting for canMoveTimer to trigger
        if(!canMove) return;

        // Get the correct unit vector and update the facing to match
        if(keyCode == KeyEvent.VK_UP) {
            nextMove = Position.UP;
            facing = 1;
        } else if(keyCode == KeyEvent.VK_DOWN) {
            nextMove = Position.DOWN;
            facing = 3;
        } else if(keyCode == KeyEvent.VK_LEFT) {
            nextMove = Position.LEFT;
            facing = 2;
        } else if(keyCode == KeyEvent.VK_RIGHT) {
            nextMove = Position.RIGHT;
            facing = 0;
        } else {
            return;
        }

        // Get the position that would be moved to and test if entry is allowed for Pacman
        Position tempPosition = new Position(position);
        tempPosition.add(nextMove);
        if(!map.canEnter(tempPosition,this,false)) {
            nextMove = Position.ZERO;
        }
    }

    /**
     * Changes the facing of Pacman. 0=UP, 1=RIGHT, 2=DOWN, 3=LEFT.
     *
     * @param facing The facing to change to.
     */
    public void setFacing(int facing) {
        this.facing = facing;
    }

    /**
     *  Gets the current facing of Pacman. 0=UP, 1=RIGHT, 2=DOWN, 3=LEFT.
     *
     * @return The current facing of Pacman.
     */
    public int getFacing() {
        return facing;
    }

    /**
     * Draws Pacman to the screen by drawing an arc with a variable angle.
     * The start angle is modified so that both sides of the mouth move
     * instead of just the jaw.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paint(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillArc(GamePanel.CELL_DIM*position.x, GamePanel.CELL_DIM* position.y,
                GamePanel.CELL_DIM, GamePanel.CELL_DIM,90*facing+openAmount/2,360-openAmount);
    }
}
