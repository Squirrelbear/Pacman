import java.util.ArrayList;
import java.util.List;

/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * GhostAI class:
 * Provides utility for managing AI movement, behaviours, and
 * represents the map as a grid of AINodes.
 */
public class GhostAI {
    /**
     * Interface to be used for the Frightened state behaviours.
     */
    interface FrightenedBehaviour {
        /**
         * Moves the associated ghost based on a frightened state.
         */
        void frightened();

        /**
         * Call when the Frightened state is begun to initialise the state.
         */
        void beginWandering();
    }

    /**
     * Interface to be used for the Chase state behaviours.
     */
    interface ChaseBehaviour {
        /**
         * Moves the associated ghost based on a chase state.
         */
        void chase();
    }

    /**
     * Interface to be used for the Chase state behaviours.
     */
    interface ScatterBehaviour {
        /**
         * Moves the associated ghost based on a scatter state.
         */
        void scatter();
    }

    /**
     * Map represented as a grid of AI Nodes to provide utility for easily moving
     * AI across the map with most of the movement logic handled.
     */
    public static AINode[][] navMap;

    /**
     * Shows/hides debug logs showing all the actions being taken by the AI.
     */
    public static boolean showAIDebug = false;

    /**
     * Populates the navMap by using the objects on the map. The edge cells of the map are excluded.
     */
    public static void generateNavMap() {
        if(navMap != null) return;
        MapObject[][] mapObjects = Map.INSTANCE.getMapObjects();
        navMap = new AINode[mapObjects.length][mapObjects[0].length];
        for(int x = 1; x < mapObjects.length-1; x++) {
            for(int y = 1; y < mapObjects[0].length-1; y++) {
                navMap[x][y] = new AINode(new Position(x,y), mapObjects);
            }
        }
    }

    /**
     * Applies a restriction to the navMap by setting the specified position to
     * not allow movement in the designated direction.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param direction Direction to modify. 0=Up, 1=Right, 2=Down, 3=Left
     */
    public static void applyNavMapRestriction(int x, int y, int direction) {
        navMap[x][y].setCanMoveDirection(direction, false);
    }

    /**
     * Call after the generateNavMap() with the default map loaded to apply all the
     * nav map restrictions.
     */
    public static void applyNavMapRestrictions() {
        // No up movement at bottom of top T
        navMap[12][11].setCanMoveDirection(0,false);
        navMap[15][11].setCanMoveDirection(0,false);

        // No up movement at bottom of bottom T
        navMap[12][23].setCanMoveDirection(0,false);
        navMap[15][23].setCanMoveDirection(0,false);

        // Do not allow access to tunnel on left/right sides
        navMap[6][14].setCanMoveDirection(3,false);
        navMap[21][14].setCanMoveDirection(1,false);

        // Left/Right doors in middle are exit only
        navMap[18][14].setCanMoveDirection(3,false);
        navMap[9][14].setCanMoveDirection(1,false);

        // Top doors in middle are exit only
        navMap[13][11].setCanMoveDirection(2,false);
        navMap[14][11].setCanMoveDirection(2,false);
    }

    /**
     * Gets a list of valid moves at the specified position on the navMap.
     *
     * @param position The position currently on the map.
     * @param currentFacing Current facing at the specified position.
     * @param preventBackMovement If this is true it will prevent movement backward from the currentFacing.
     * @return A list of valid moves that can be performed at the specified position.
     */
    public static List<Position> getValidMoves(Position position, int currentFacing, boolean preventBackMovement) {
        if(position.x < 1 || position.y < 1 || position.x > navMap.length-2 || position.y > navMap[0].length-2)
        {
            // THIS SHOULD NEVER HAPPEN!!
            System.out.println("INVALID POSITION! " + position + " F: " + currentFacing + " pBM: " + preventBackMovement);
            return new ArrayList<>();
        }

        return navMap[position.x][position.y].getValidMoves(currentFacing, preventBackMovement);
    }

    /**
     * Gets the best move from a list of positions based on how they move relative to the "to" position.
     *
     * @param from Position of the moving object.
     * @param to Target position to attempt to move to.
     * @param validMoves A list of valid moves from the current position.
     * @param preferLowerDistance Determines if the minimum or maximum distance is used.
     * @return A single unit vector representing the movement to be used for a movement.
     */
    public static Position getBestMoveFromList(Position from, Position to, List<Position> validMoves, boolean preferLowerDistance) {
        // Default the best move to the first item in the list
        Position bestMove = validMoves.get(0);
        Position tempPosition = new Position(from);
        tempPosition.add(bestMove);
        double bestValue = tempPosition.distanceTo(to);

        // Test all other moves to check if they are better options
        for(int i = 1; i < validMoves.size(); i++) {
            tempPosition = new Position(from);
            tempPosition.add(validMoves.get(i));
            double tempValue = tempPosition.distanceTo(to);
            // Use the correct min or max depending on preferLowerDistance to choose positions.
            if(preferLowerDistance && tempValue < bestValue) {
                bestValue = tempValue;
                bestMove = validMoves.get(i);
            } else if(!preferLowerDistance && tempValue > bestValue) {
                bestValue = tempValue;
                bestMove = validMoves.get(i);
            }
        }
        return bestMove;
    }

    /**
     * Makes the ghost move based on a target position.
     *
     * @param ghost Reference to the Ghost to be moved.
     * @param target The position to move to.
     * @param preventBackMovement Determines whether the ghost can go backwards from the current facing.
     * @param preferLowerDistance Used to choose running toward or away from the target.
     */
    public static void moveGhostUsingTarget(Ghost ghost, Position target, boolean preventBackMovement, boolean preferLowerDistance) {
        List<Position> validMoves = GhostAI.getValidMoves(ghost.getPosition(),ghost.getFacing(),preventBackMovement);
        if(validMoves.size() == 0) {
            return;
        } else if(validMoves.size() == 1) {
            ghost.move(validMoves.get(0));
        } else {
            Position bestMove = GhostAI.getBestMoveFromList(ghost.getPosition(), target, validMoves,preferLowerDistance);
            if(showAIDebug) {
                System.out.print("Moving ghost " + ghost + " at: " + ghost.getPosition() + " with " + bestMove);
            }
            ghost.move(bestMove);
            if(showAIDebug) {
                System.out.println(" to " + ghost.getPosition());
            }
        }
    }

    /**
     * Gets the matching Position to go with a facing value.
     * 0=UP, 1=RIGHT, 2=DOWN, 3=LEFT
     *
     * @param facing The facing to convert into a Position.
     * @return A Position related to 0 to 3 or ZERO.
     */
    public static Position facingToPosition(int facing) {
        switch (facing) {
            case 0: return new Position(Position.UP);
            case 1: return new Position(Position.RIGHT);
            case 2: return new Position(Position.DOWN);
            case 3: return new Position(Position.LEFT);
        }
        return new Position(Position.ZERO);
    }

    /**
     * Gets the offset position based on a current pos, a direction of facing,
     * and then multiples of the unit vector  from the facing offset number of times.
     *
     * @param pos The position of the object.
     * @param facing The facing to offset from the object.
     * @param offset The amount to offset by in multiples of the facing.
     * @return A new position offset by the number of cells in the facing direction.
     */
    public static Position getOffsetFromFacing(Position pos, int facing, int offset) {
        Position unitVector = facingToPosition(facing);
        unitVector.multiply(offset);
        Position newPos = new Position(pos);
        newPos.add(unitVector);
        return newPos;
    }
}

