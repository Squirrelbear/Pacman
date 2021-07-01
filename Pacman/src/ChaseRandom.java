/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * ChaseRandom class:
 * Defines a chase behaviour that will appear to be random, but is not.
 * When it is further than 8 units away from pacman the aggressive chase
 * behaviour is used. Otherwise will move toward the bottom left corner.
 */
public class ChaseRandom implements GhostAI.ChaseBehaviour {
    /**
     * Reference to the Ghost using this behaviour.
     */
    private Ghost ghost;
    private GhostAI.ChaseBehaviour farBehaviour;
    private GhostAI.ScatterBehaviour closeBehaviour;

    /**
     * Initialises the chase ready for movements using chase.
     *
     * @param ghost The Ghost that will be using this behaviour.
     */
    public ChaseRandom(Ghost ghost) {
        this.ghost = ghost;
        farBehaviour = new ChaseAggressive(ghost);
        closeBehaviour = new ScatterBottomLeftCorner(ghost);
    }

    /**
     * Moves directly toward Pacman if further than 8 units away.
     * Otherwise will scatter back toward the bottom left corner.
     */
    @Override
    public void chase() {
        if(ghost.getPosition().distanceTo(Map.INSTANCE.getPacman().getPosition()) > 8) {
            farBehaviour.chase();
        } else {
            closeBehaviour.scatter();
        }
    }
}
