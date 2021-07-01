import java.awt.*;

/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * DoorObject class:
 * Shows a pink rectangle that can only be passed through by a ghost.
 */
public class DoorObject extends MapObject {
    /**
     * Initialises the door so it can only be entered by ghosts.
     *
     * @param position The position on the map.
     */
    public DoorObject(Position position) {
        super(position, ObjectType.Door, EnterType.GhostOnly);
    }

    /**
     * Draws a pink rectangle to fill the position on the grid.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        g.setColor(Color.PINK);
        g.fillRect(GamePanel.CELL_DIM*position.x, GamePanel.CELL_DIM* position.y,
                GamePanel.CELL_DIM, GamePanel.CELL_DIM);
    }
}
