import java.awt.*;

/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * Ghost class:
 * Defines a ghost with movement behaviours, and how they are
 * represented visually.
 */
public class Ghost extends MapObject {
    /**
     * The four states of a ghost.
     * Inactive: Not currently moving and must have state changed from an external event.
     * Chase: Chases for duration in CHASE_DURATION using the chaseBehaviour. Then swaps to Scatter.
     * Scatter: Scatters for duration in SCATTER_DURATION using the scatterBehaviour. Then swaps to Chase.
     * Frightened: Pauses the state of Chase/Scatter and fears for FEAR_DURATION using the frightenedBehaviour.
     * After leaving frightened it will go back to the previous state.
     */
    public enum GhostState { Inactive, Chase, Scatter, Frightened }

    /**
     * The current state of the ghost.
     */
    private GhostState ghostState;

    /**
     * Ghost number is used to define the colour and associated behaviours.
     */
    private int ghostNumber;
    /**
     * Colours for ghosts 0 to 3 and 4 is used for the frightened colour.
     */
    private final Color[] ghostColours = {Color.RED, Color.CYAN, new Color(255,184,255),
                                          new Color(255, 184, 82),
                                            new Color(5, 5, 144)};
    /**
     * Animation offset for the eye animation.
     */
    private int animValue;
    /**
     * Direction of eye motion. Will flip back and forward over time.
     */
    private int animDirection;
    /**
     * A timer to track when the eye will next move.
     */
    private int counterTillEyeMove;
    /**
     * If true will make the frightened ghost show with white instead of blue.
     */
    private boolean flashGhost;

    /**
     * Reference to a FrightenedBehaviour that can be used to move the ghost while in the Frightened state.
     */
    private GhostAI.FrightenedBehaviour frightenedBehaviour;
    /**
     * Reference to a ScatterBehaviour that can be used to move the ghost while in the Scatter state.
     */
    private GhostAI.ScatterBehaviour scatterBehaviour;
    /**
     * Reference to a ChaseBehaviour that can be used to move the ghost while in the Chase state.
     */
    private GhostAI.ChaseBehaviour chaseBehaviour;

    /**
     * Current facing direction. 0=Up, 1=Right, 2=Down, 3=Left
     */
    private int facing;

    /**
     * Timer to be used to make the ghost move using a behaviour.
     */
    private ActionTimer canMoveTimer;
    /**
     * Set true when the canMoveTimer triggers and allows the ghost to move.
     */
    private boolean canMove;
    /**
     * Timer used to swap between Scatter and Chase states.
     * Not updated during Inactive or Frightened states.
     */
    private ActionTimer nextStateTimer;
    /**
     * Timer to track duration of frightened state.
     */
    private ActionTimer fearTimer;
    /**
     * Timer to swap the flashGhost variable during the frightened state to swap between blue and white.
     */
    private ActionTimer flashTimer;
    /**
     * State before frightened to allow return after the state ends.
     */
    private GhostState previousState;

    /**
     * Duration in milliseconds of the Scatter state.
     */
    private final int SCATTER_DURATION = 7000;
    /**
     * Duration in milliseconds of the Chase state.
     */
    private final int CHASE_DURATION = 20000;
    /**
     * Duration in milliseconds of the Frightened state.
     */
    private final int FEAR_DURATION = 10000;
    /**
     * Duration in milliseconds between movements.
     */
    private final int TIME_BETWEEN_MOVES = 200;
    /**
     * Duration in milliseconds between movements while in the Frightened state.
     */
    private final int TIME_BETWEEN_MOVES_WHILE_FEARED = 280;
    /**
     * Duration in milliseconds between changing the flashTimer.
     */
    private final int TIME_BETWEEN_FEAR_FLASH = 160;

    /**
     * The position where the ghost originally stated. Used for resetting after being eaten.
     */
    private Position startPosition;

    /**
     * Creates a ghost setting up its initial state, associated timers, and
     * configuring AI behaviours ready for movement.
     *
     * @param position The position where the ghost starts.
     * @param ghostNumber Number of the ghost to dictate what behaviour and colours it has.
     */
    public Ghost(Position position, int ghostNumber) {
        super(position, ObjectType.Ghost, EnterType.Everyone);
        startPosition = new Position(position);
        this.ghostNumber = ghostNumber;
        // Eyes state centred.
        animValue = 0;
        // Eyes start moving right
        animDirection = 1;
        // Eyes begin moving right away
        counterTillEyeMove = 0;
        // Only the red ghost starts moving at the beginning.
        ghostState = ghostNumber == 0 ? GhostState.Scatter : GhostState.Inactive;
        // Starts by facing up.
        facing = 0;
        // Can move right away
        canMove = true;
        // Initialise all timers ready for use.
        canMoveTimer = new ActionTimer(TIME_BETWEEN_MOVES);
        nextStateTimer = new ActionTimer(SCATTER_DURATION);
        fearTimer = new ActionTimer(FEAR_DURATION);
        flashTimer = new ActionTimer(TIME_BETWEEN_FEAR_FLASH);
        // Assign behaviours for movement
        configureAI();
    }

    /**
     * Animate the eyes, and if in any state that is active the state will be updated.
     * And then movement will be updated based on the current state.
     */
    public void update() {
        animateEyes();

        if(ghostState == GhostState.Inactive) return;
        updateState();
        updateMovement();
    }

    /**
     * Resets the ghost back to the original state including all timers.
     */
    @Override
    public void reset() {
        super.reset();
        ghostState = ghostNumber == 0 ? GhostState.Scatter : GhostState.Inactive;
        flashTimer.reset();
        fearTimer.reset();
        nextStateTimer.reset();
        canMoveTimer.reset();
        facing = 0;
    }

    /**
     * Resets the ghost back to the start position and begins with a Scatter state.
     */
    public void resetToStart() {
        position = new Position(startPosition);
        flashTimer.reset();
        fearTimer.reset();
        nextStateTimer.reset();
        canMoveTimer.reset();
        facing = 0;
        setState(GhostState.Scatter);
    }

    /**
     * Sets the state and configures required variables during the transition.
     *
     * @param state The new state to apply.
     */
    public void setState(GhostState state) {
        if(state == GhostState.Frightened) {
            // Can't be frightened when not yet active
            if(ghostState == GhostState.Inactive) {
                return;
            } else {
                // If the transition to frightened came from a different state
                // retain that previous state to return back after.
                if(ghostState != GhostState.Frightened) {
                    previousState = ghostState;
                }
                // Allow the ghost to double back for running away immediately
                frightenedBehaviour.beginWandering();
            }
            fearTimer.reset();
        } else if(state == GhostState.Chase) {
            nextStateTimer.setTimer(CHASE_DURATION);
        } else if(state == GhostState.Scatter) {
            nextStateTimer.setTimer(SCATTER_DURATION);
        }

        this.ghostState = state;
    }

    /**
     * Gets the current state of the Ghost.
     *
     * @return The current GhostState that is active.
     */
    public GhostState getGhostState() {
        return ghostState;
    }

    /**
     * Gets the current facing of the Ghost.
     *  0=Up, 1=Right, 2=Down, 3=Left
     *
     * @return The current facing of the ghost.
     */
    public int getFacing() {
        return facing;
    }

    /**
     * Moves the ghost in the specified direction and updates the facing to represent the new direction.
     *
     * @param directionVector Unit vector to move in.
     */
    public void move(Position directionVector) {
        if(directionVector.equals(Position.UP)) facing = 0;
        else if(directionVector.equals(Position.RIGHT)) facing = 1;
        else if(directionVector.equals(Position.DOWN)) facing = 2;
        else if(directionVector.equals(Position.LEFT)) facing = 3;
        position.add(directionVector);
    }

    /**
     * Animates the eyes by delaying time between updates and then rolling between 2 and -2.
     */
    private void animateEyes() {
        if(counterTillEyeMove > 0) {
            counterTillEyeMove--;
        } else {
            animValue += animDirection;
            if (animValue == 2 || animValue == -2) animDirection = -animDirection;
            counterTillEyeMove = 2;
        }
    }

    /**
     * Updates the current state of the Ghost. Updates associated timers for the state
     * and then if the timer has been triggered the state will be changed as necessary.
     */
    private void updateState() {
        if(ghostState == GhostState.Frightened) {
            flashTimer.update();
            if(flashTimer.isTriggered()) {
                // Swap between Blue and White, spends longer in Blue state.
                flashGhost = !flashGhost;
                flashTimer.setTimer(flashGhost ? TIME_BETWEEN_FEAR_FLASH / 2 : TIME_BETWEEN_FEAR_FLASH);
            }
            fearTimer.update();
            if(fearTimer.isTriggered()) {
                setState(previousState);
            }
        } else {
            nextStateTimer.update();
            if(nextStateTimer.isTriggered()) {
                // Toggle to other state
                setState(ghostState == GhostState.Scatter ? GhostState.Chase : GhostState.Scatter);
            }
        }
    }

    /**
     * Updates the timer for movement, and if it has triggered movement is handled by the
     * correct behaviour depending on the current state.
     */
    private void updateMovement() {
        if(!canMove) {
            canMoveTimer.update();
            if(canMoveTimer.isTriggered()) {
                // Use a slower speed while frightened to make easier to catch
                if(ghostState == GhostState.Frightened)
                    canMoveTimer.setTimer(TIME_BETWEEN_MOVES_WHILE_FEARED);
                else
                    canMoveTimer.setTimer(TIME_BETWEEN_MOVES);
                canMove = true;
            }
        } else {
            switch (ghostState) {
                case Frightened:
                    frightenedBehaviour.frightened();
                    break;
                case Chase:
                    chaseBehaviour.chase();
                    break;
                case Scatter:
                    scatterBehaviour.scatter();
                    break;
            }
            canMove = false;
        }
    }

    /**
     * Draws a ghost with correct colour with an oval and a rectangle. Along with a pair of
     * animated eyes that will move left and right continually. If frightened a mouth is also shown.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    @Override
    public void paint(Graphics g) {
        // Draw background circle
        if(ghostState == GhostState.Frightened) g.setColor(flashGhost ? Color.WHITE : ghostColours[4]);
        else g.setColor(ghostColours[ghostNumber]);
        g.fillOval(GamePanel.CELL_DIM*position.x, GamePanel.CELL_DIM* position.y,
                GamePanel.CELL_DIM, GamePanel.CELL_DIM);
        // Draw background bottom rect
        g.fillRect(GamePanel.CELL_DIM*position.x, GamePanel.CELL_DIM* position.y+GamePanel.CELL_DIM/2,
                GamePanel.CELL_DIM, GamePanel.CELL_DIM/2);

        // Draw eye background
        g.setColor(Color.WHITE);
        g.fillOval(GamePanel.CELL_DIM*position.x+GamePanel.CELL_DIM/4,
                GamePanel.CELL_DIM* position.y+GamePanel.CELL_DIM/4-3,6,6);
        g.fillOval(GamePanel.CELL_DIM*position.x+GamePanel.CELL_DIM*3/4-2,
                GamePanel.CELL_DIM* position.y+GamePanel.CELL_DIM/4-3,6,6);
        // Draw animated black dots inside eyes
        g.setColor(Color.BLACK);
        g.fillOval(GamePanel.CELL_DIM*position.x+GamePanel.CELL_DIM/4+2+animValue,
                GamePanel.CELL_DIM* position.y+GamePanel.CELL_DIM/4-1,3,3);
        g.fillOval(GamePanel.CELL_DIM*position.x+GamePanel.CELL_DIM*3/4+animValue,
                GamePanel.CELL_DIM* position.y+GamePanel.CELL_DIM/4-1,3,3);

        if(ghostState == GhostState.Frightened) {
            g.drawLine(GamePanel.CELL_DIM*position.x+GamePanel.CELL_DIM/4+2,
                    GamePanel.CELL_DIM* position.y+GamePanel.CELL_DIM*3/4-1,
                    GamePanel.CELL_DIM*position.x+GamePanel.CELL_DIM*3/4,
                    GamePanel.CELL_DIM* position.y+GamePanel.CELL_DIM*3/4-1);
        }
    }

    /**
     * Gets a simple string representation of the ghost.
     *
     * @return A string in the form "name (F: facing S: state)"
     */
    @Override
    public String toString() {
        return getGhostName() + " (F:" + facing + " S:" + ghostState +")";
    }

    /**
     * Gets a string colour name version of the ghostNumber.
     *
     * @return The name of the ghost or Unknown.
     */
    private String getGhostName() {
        String ghostName = "Unknown";
        switch(ghostNumber) {
            case 0: ghostName = "Red"; break;
            case 1: ghostName = "Cyan"; break;
            case 2: ghostName = "Pink"; break;
            case 3: ghostName = "Orange"; break;
        }
        return ghostName;
    }

    /**
     * Sets the behaviours for movement dependent on the ghost's number.
     */
    private void configureAI() {
        // Same frightened behaviour is shared for all ghosts
        frightenedBehaviour = new FrightenedWandering(this);
        switch(ghostNumber) {
            case 0: // RED
                chaseBehaviour = new ChaseAggressive(this);
                scatterBehaviour = new ScatterTopRightCorner(this);
                break;
            case 1: // CYAN
                chaseBehaviour = new ChasePatrol(this);
                scatterBehaviour = new ScatterBottomRightCorner(this);
                break;
            case 2: // PINK
                chaseBehaviour = new ChaseAmbush(this);
                scatterBehaviour = new ScatterTopLeftCorner(this);
                break;
            case 3: // ORANGE
                chaseBehaviour = new ChaseRandom(this);
                scatterBehaviour = new ScatterBottomLeftCorner(this);
                break;
        }
    }
}
