/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * ChaseAggressive class:
 * Behaviour implementation that will chase the player as directly as possible.
 */
public class ChaseAggressive implements GhostAI.ChaseBehaviour {
    /**
     * Reference to the Ghost using this behaviour.
     */
    private Ghost ghost;

    /**
     * Initialises the chase ready for movements using chase.
     *
     * @param ghost The Ghost that will be using this behaviour.
     */
    public ChaseAggressive(Ghost ghost) {
        this.ghost = ghost;
    }

    /**
     * Moves the ghost if possible based on chasing as closely as possible toward the player.
     */
    @Override
    public void chase() {
        GhostAI.moveGhostUsingTarget(ghost, Map.INSTANCE.getPacman().getPosition(), true, true);
    }
}
