import java.util.ArrayList;
import java.util.List;

/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * AINode class:
 * Defines a node useful for pathfinding related to
 * the map based on the Map giving a quick representation of
 * valid movements from every node to the next.
 */
class AINode {
    /**
     * The position of this node on the grid.
     */
    public Position position;
    /**
     * Can move in direction to generate a list of canMoveTo.
     * 0=Up,1=Right,2=Down,3=Left
     */
    public boolean[] canMoveDirection;
    /**
     * List of all movements that can be applied from this position.
     */
    public List<Position> canMoveTo;

    /**
     * Checks for the adjacent cells that can be entered and initialises a list of
     * valid moves that can be accessed.
     *
     * @param position The position of this node on the grid.
     * @param mapObjects Reference to already filled map to determine which grid positions can be entered.
     */
    public AINode(Position position, MapObject[][] mapObjects) {
        this.position = position;
        this.canMoveDirection = new boolean[] {
                canMoveDirection(0,mapObjects), canMoveDirection(1,mapObjects),
                canMoveDirection(2,mapObjects), canMoveDirection(3,mapObjects)};
        canMoveTo = new ArrayList<>();
        updateValidMoves();
    }

    /**
     * Updates the status of movement in a direction. Then updates the valid move list.
     *
     * @param direction Direction to modify. 0=Up,1=Right,2=Down,3=Left
     * @param value The value to set the direction to.
     */
    public void setCanMoveDirection(int direction, boolean value) {
        canMoveDirection[direction] = value;
        updateValidMoves();
    }

    /**
     * Gets a list of all valid moves from the current node as a list of directions.
     * This will exclude the backward movement if preventBackMovement is true.
     *
     * @param currentFacing The current facing direction to be used for preventing backward movement.
     * @param preventBackMovement If true it will exclude the backward movement from the current facing.
     * @return A list of all valid moves at the current node.
     */
    public List<Position> getValidMoves(int currentFacing, boolean preventBackMovement) {
        List<Position> result = new ArrayList<>();
        Position backwardMovement = GhostAI.facingToPosition((currentFacing + 2) % 4);
        for(Position move : canMoveTo) {
            if(!preventBackMovement || !move.equals(backwardMovement)) {
                result.add(move);
            }
        }
        return result;
    }

    /**
     * Creates a list of direction vectors for valid movement based on the canMoveDirection array.
     */
    private void updateValidMoves() {
        canMoveTo.clear();
        for(int i = 0; i < 4; i++) {
            if(canMoveDirection[i]) {
                canMoveTo.add(GhostAI.facingToPosition(i));
            }
        }
    }


    /**
     * Checks the enter conditions of the adjacent cell in facing direction on the map for
     * entry to everyone or ghost only.
     *
     * @param facing The facing direction to move.
     * @param mapObjects Reference to the map to evaluate the enter type of the adjacent cell in facing direction.
     * @return True if the ghost can move in the specified direction.
     */
    private boolean canMoveDirection(int facing, MapObject[][] mapObjects) {
        Position newPos = new Position(position);
        newPos.add(GhostAI.facingToPosition(facing));
        MapObject.EnterType enterType = mapObjects[newPos.x][newPos.y].getEnterType();
        return enterType == MapObject.EnterType.Everyone || enterType == MapObject.EnterType.GhostOnly;
    }
}