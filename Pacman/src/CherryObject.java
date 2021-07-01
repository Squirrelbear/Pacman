import java.awt.*;

/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * Cherries appear at 70 and 170 pickups. They are hidden and not
 * collectable until reaching these breakpoints. Collecting a cherry
 * will make it hide again.
 */
public class CherryObject extends MapObject{
    /**
     * True if the player has already collected this cherry.
     */
    private boolean collected;
    /**
     * True if the cherry has been revealed from an external trigger.
     */
    private boolean revealed;

    /**
     * Creates a cherry that can be entered by anyone and starts as being hidden.
     *
     * @param position The position of the cherry on the map.
     */
    public CherryObject(Position position) {
        super(position, ObjectType.Cherry, EnterType.Everyone);
        collected = false;
        revealed = false;
    }

    /**
     * Resets the collected and revealed statuses.
     */
    @Override
    public void reset() {
        super.reset();
        collected = false;
        revealed = false;
    }

    /**
     * Called when an object enters this object to trigger effects if necessary.
     * If the player enters this object while it is revealed and not collected yet,
     * it will make itself as being collected and award 150 score.
     *
     * @param enteringObject The object that has just entered this map object.
     */
    @Override
    public void onEnter(MapObject enteringObject) {
        if(!collected && revealed && enteringObject instanceof Pacman) {
            collected = true;
            Map.INSTANCE.addBonusScore(150);
        }
    }

    /**
     * Makes the cherry visible.
     */
    public void reveal() {
        revealed = true;
    }

    /**
     * Draws a cherry consisting of two red balls and two green lines.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        if(!revealed || collected) return;
        // Cherry balls
        g.setColor(Color.RED);
        g.fillOval(GamePanel.CELL_DIM*position.x+GamePanel.CELL_DIM/4,
                GamePanel.CELL_DIM*position.y+GamePanel.CELL_DIM*3/4-4,
                7,7);
        g.fillOval(GamePanel.CELL_DIM*position.x+GamePanel.CELL_DIM*3/4,
                GamePanel.CELL_DIM*position.y+GamePanel.CELL_DIM*3/4-4,
                7,7);

        // Green stems
        g.setColor(Color.GREEN);
        g.drawLine(GamePanel.CELL_DIM*position.x+GamePanel.CELL_DIM/4+4,
                GamePanel.CELL_DIM*position.y+GamePanel.CELL_DIM*3/4-4,
                GamePanel.CELL_DIM*position.x+GamePanel.CELL_DIM/2+3,
                GamePanel.CELL_DIM*position.y+GamePanel.CELL_DIM/4);
        g.drawLine(GamePanel.CELL_DIM*position.x+GamePanel.CELL_DIM*3/4+4,
                GamePanel.CELL_DIM*position.y+GamePanel.CELL_DIM*3/4-4,
                GamePanel.CELL_DIM*position.x+GamePanel.CELL_DIM/2+3,
                GamePanel.CELL_DIM*position.y+GamePanel.CELL_DIM/4);
    }
}
