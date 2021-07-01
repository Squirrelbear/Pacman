import java.awt.*;

/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * WallObject class:
 * A wall object that acts as a generic obstacle.
 */
public class WallObject extends MapObject {
    /**
     * Creates the wall object set to not let any objects enter it.
     *
     * @param position Position of the object on the map.
     */
    public WallObject(Position position) {
        super(position, ObjectType.Wall, EnterType.None);
    }

    /**
     * Draws the wall represented as a solid block of blue colour.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(GamePanel.CELL_DIM*position.x, GamePanel.CELL_DIM* position.y,
                        GamePanel.CELL_DIM, GamePanel.CELL_DIM);
    }
}
