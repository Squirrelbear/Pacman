import java.awt.*;

/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * PowerupObject class:
 * Represents a powerup that changes the game state.
 * When this object is entered by Pacman it will cause all
 * currently active ghosts to frighten.
 */
public class PowerupObject extends MapObject {
    /**
     * Current collection status. If this is true it can't
     * be collected again until it is reset. While collected it is not visible.
     */
    private boolean collected;

    /**
     * Initialises the Powerup object so it can be entered by everyone
     * makes it not collected by default.
     *
     * @param position The position of this object on the map.
     */
    public PowerupObject(Position position) {
        super(position, ObjectType.PowerUp, EnterType.Everyone);
        collected = false;
    }

    /**
     * Resets the collected state to false.
     */
    @Override
    public void reset() {
        super.reset();
        collected = false;
    }

    /**
     * Called when an object enters this object. If the entering object is
     * Pacman it will mark it as collected and tell the map to frighten all the ghosts.
     *
     * @param enteringObject The object that is entering this object.
     */
    @Override
    public void onEnter(MapObject enteringObject) {
        if(!collected && enteringObject instanceof Pacman) {
            collected = true;
            Map.INSTANCE.frightenGhosts();
        }
    }

    /**
     * Draws the powerup as a simple white circle if it has not been collected yet.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        if(collected) return;
        g.setColor(Color.WHITE);
        g.fillOval(GamePanel.CELL_DIM*position.x,
                GamePanel.CELL_DIM* position.y,
                GamePanel.CELL_DIM, GamePanel.CELL_DIM);
    }
}
