/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * ChasePatrol class:
 * Defines a movement behaviour that takes into account both the
 * red ghost and an offset position from Pacman to determine a target position.
 */
public class ChasePatrol implements GhostAI.ChaseBehaviour {
    /**
     * Reference to the Ghost using this behaviour.
     */
    private Ghost ghost;

    /**
     * Initialises the chase ready for movements using chase.
     *
     * @param ghost The Ghost that will be using this behaviour.
     */
    public ChasePatrol(Ghost ghost) {
        this.ghost = ghost;
    }

    /**
     * Finds a target to move to based on taking the position two units in front of Pacman's current facing.
     * Then gets the direction vector from the red ghost to that offset from Pacman.
     * The direction vector is doubled to give the target position. This target position is then pursued.
     */
    @Override
    public void chase() {
        Position pacmanOffset = GhostAI.getOffsetFromFacing(Map.INSTANCE.getPacman().getPosition(),
                Map.INSTANCE.getPacman().getFacing(), 2);
        Position redPos = Map.INSTANCE.getGhost(0).getPosition();
        // Calculate the direction vector from pacmanOffset
        Position directionVector = new Position(pacmanOffset);
        directionVector.subtract(redPos);
        // Double the direction vector to get the distance offset
        directionVector.multiply(2);
        // Apply the distance offset to the red position to get the target cell
        directionVector.add(redPos);
        if(GhostAI.showAIDebug)
            System.out.println("Target: " + directionVector + " P: " + pacmanOffset + " R: " + redPos);
        GhostAI.moveGhostUsingTarget(ghost, directionVector, true, true);
    }
}
