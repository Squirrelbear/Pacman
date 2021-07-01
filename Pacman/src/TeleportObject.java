/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * TeleportObject class:
 * Defines a map object that acts as an invisible teleport point.
 * Entering the teleport will teleport to a specified location.
 */
public class TeleportObject extends MapObject {
    /**
     * The position to teleport to when entered.
     */
    private Position teleportTo;
    /**
     * The facing to look at after teleporting.
     */
    private int facingAfterTeleport;

    /**
     * Sets up the TeleportObject to be enterable by Pacman.
     *
     * @param position The position of the teleport.
     * @param teleportTo The position the Pacman is sent to after walking into this object.
     * @param facingAfterTeleport The direction to face after being teleported.
     */
    public TeleportObject(Position position, Position teleportTo, int facingAfterTeleport) {
        super(position, ObjectType.Teleport, EnterType.PacmanOnly);
        this.teleportTo = teleportTo;
        this.facingAfterTeleport = facingAfterTeleport;
    }

    /**
     * Called when an object enters this teleport point.
     * If the object is Pacman it will move the position of Pacman to the
     * teleportTo position and then modifies the facing.
     *
     * @param enteringObject The object that is entering this object.
     */
    @Override
    public void onEnter(MapObject enteringObject) {
        if(enteringObject instanceof Pacman) {
            Pacman pacman = (Pacman)enteringObject;
            pacman.setPosition(new Position(teleportTo));
            pacman.setFacing(facingAfterTeleport);
        }
    }
}
