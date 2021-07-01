import java.awt.*;

/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * CollectableObject class:
 * Represents the small pickup objects that are all over the maps.
 * Starts as visible and hides once collected by the player.
 */
public class CollectableObject extends MapObject {
    /**
     * Cached representation of the circle width.
     */
    private int objectWidth;
    /**
     * Represents if this has been collected by the player. Once collected it will hide the object.
     */
    private boolean collected;

    /**
     * Initialises the object to be not collected ready and so it can be entered by everyone.
     *
     * @param position The position of the object on the map.
     */
    public CollectableObject(Position position) {
        super(position, ObjectType.Score, EnterType.Everyone);
        collected = false;
        objectWidth = GamePanel.CELL_DIM/4;
    }

    /**
     * Resets the collection status back to not collected.
     */
    @Override
    public void reset() {
        super.reset();
        collected = false;
    }

    /**
     * If the object has not been collected and the entering object is the player
     * it will collect this object and increase the number of collected objects.
     *
     * @param enteringObject The object entering this object.
     */
    @Override
    public void onEnter(MapObject enteringObject) {
        if(!collected && enteringObject instanceof Pacman) {
            collected = true;
            Map.INSTANCE.increaseCollected();
        }
    }

    /**
     * Draws a small white circle if it has not been collected yet.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        if(collected) return;
        g.setColor(Color.WHITE);
        g.fillOval(GamePanel.CELL_DIM*position.x + GamePanel.CELL_DIM/2-objectWidth/2,
                GamePanel.CELL_DIM* position.y+ GamePanel.CELL_DIM/2-objectWidth/2,
                objectWidth, objectWidth);
    }
}
