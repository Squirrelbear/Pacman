/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * ChaseAmbush class:
 * Behaviour implementation that will chase the grid position four cells in front of the player.
 */
public class ChaseAmbush implements GhostAI.ChaseBehaviour {
    /**
     * Reference to the Ghost using this behaviour.
     */
    private Ghost ghost;

    /**
     * Initialises the chase ready for movements using chase.
     *
     * @param ghost The Ghost that will be using this behaviour.
     */
    public ChaseAmbush(Ghost ghost) {
        this.ghost = ghost;
    }

    /**
     * Locates the position 4 cells in front of Pacman's current direction
     * and tries to move efficiently to that position.
     */
    @Override
    public void chase() {
        Position targetPos = GhostAI.getOffsetFromFacing(Map.INSTANCE.getPacman().getPosition(),
                                                         Map.INSTANCE.getPacman().getFacing(), 4);
        GhostAI.moveGhostUsingTarget(ghost, targetPos, true, true);
    }
}
