/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * ScatterTopLeftCorner class:
 * Defines a scatter behaviour for use by the Ghosts that goes to a corner.
 */
public class ScatterTopLeftCorner implements GhostAI.ScatterBehaviour {
    /**
     * Reference to the Ghost the Behaviour is being used for.
     */
    private Ghost ghost;
    /**
     * Reference to the scatter position.
     */
    private Position scatterTarget;

    /**
     * Configures the behaviour with setting the scatter target to match the class.
     *
     * @param ghost Reference to the Ghost the Behaviour is being used for.
     */
    public ScatterTopLeftCorner(Ghost ghost) {
        this.ghost = ghost;
        scatterTarget = new Position(1, 1);
    }

    /**
     * Scatters by attempting to always move the most efficient way toward the scatter target.
     */
    @Override
    public void scatter() {
        GhostAI.moveGhostUsingTarget(ghost, scatterTarget, true, true);
    }
}
