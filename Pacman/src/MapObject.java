import java.awt.*;

/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * MapObject class:
 * Defines a generic MapObject to be inherited by all the different object types.
 */
public class MapObject {
    /**
     * Object types to distinguish the unique types.
     */
    public enum ObjectType { Pacman, Ghost, Teleport, PowerUp, Score, Door, Wall, Cherry, Empty }

    /**
     * Enter type is used to identify what other objects can enter this object.
     */
    public enum EnterType { Everyone, PacmanOnly, GhostOnly, None }

    /**
     * Position where the object is located.
     */
    protected Position position;
    /**
     * Type of the object of this object.
     */
    protected ObjectType objectType;
    /**
     * Position to be used during a reset.
     */
    protected Position defaultPosition;
    /**
     * The entry requirements of this map object.
     */
    protected EnterType enterType;

    /**
     * Creates a MapObject using the provided properties.
     *
     * @param position The Position to place the object at.
     * @param objectType The type of the object.
     * @param enterType The enter type to restrict entry.
     */
    public MapObject(Position position, ObjectType objectType, EnterType enterType) {
        this.position = new Position(position);
        this.defaultPosition = new Position(position);
        this.objectType = objectType;
        this.enterType = enterType;
    }

    /**
     * Modifies the old position of the MapObject to this new position.
     *
     * @param newPosition The new position to set this object to.
     */
    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    /**
     * Gets the current Position of the MapObject.
     *
     * @return The current Position of the MapObject.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the object type of the MapObject.
     *
     * @return The object type.
     */
    public ObjectType getObjectType() {
        return objectType;
    }

    /**
     * Gets the enter type representing any restrictions on entry.
     *
     * @return The current enter type.
     */
    public EnterType getEnterType() { return enterType; }

    /**
     * Resets the object back to its default position.
     * Can be overridden to provide additional reset parameters.
     */
    public void reset() {
        this.position = new Position(defaultPosition);
    }

    /**
     * Empty method to allow MapObjects to override this and provide
     * interaction when a moving MapObject enters.
     *
     * @param enteringObject The object that is entering this object.
     */
    public void onEnter(MapObject enteringObject) {
        // do nothing by default
    }

    /**
     * Empty method to allow MapObjects to be drawn that can be overridden
     * by inheriting objects as needed.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paint(Graphics g) {
        // draw nothing by default
    }
}
