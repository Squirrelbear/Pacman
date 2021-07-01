/**
 * Pacman
 * Author: Peter Mitchell
 *
 * FrightenedWandering class:
 * Provides a frightened behaviour that tries to run the ghost away from the player.
 */
public class FrightenedWandering implements GhostAI.FrightenedBehaviour {
    /**
     * Reference to the ghost using the behaviour.
     */
    private Ghost ghost;
    /**
     * Used to allow the ghost to move backwards on their first movement after being frightened.
     */
    private boolean firstUpdate;

    /**
     * Creates the behaviour by setting the reference to the ghost.
     *
     * @param ghost Reference to the ghost using the behaviour.
     */
    public FrightenedWandering(Ghost ghost) {
        this.ghost = ghost;
        firstUpdate = true;
    }

    /**
     * Moves in a direction away from the player. The first movement after beginWandering() the ghost can
     * double back and reverse direction. After that it has to conform to normal movement rules.
     */
    @Override
    public void frightened() {
        GhostAI.moveGhostUsingTarget(ghost,Map.INSTANCE.getPacman().getPosition(), !firstUpdate, false);
        if(firstUpdate) {
            firstUpdate = false;
        }
    }

    /**
     * Used to allow the ghost to double back on itself to run away from the player on their first update.
     */
    @Override
    public void beginWandering() {
        firstUpdate = true;
    }
}
