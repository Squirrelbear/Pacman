import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * Map class:
 * Maintains all of the data related to what is on a Map.
 * Includes a grid of map objects, references to objects that move
 * around on it including Pacman and Ghosts, and the objects that
 * are influenced by collection for the cherries.
 */
public class Map {
    /**
     * The states that the Map can be in.
     * Running: Default state when the game is running and continuing to update.
     * GameOver: When the Pacman has been collided with by a ghost causing a game over.
     * GameWon: When the Pacman has eaten all the collectable objects to win the game.
     * State can transition from running to the other states when the event occurs.
     * R can be used to restart and reset the state back to Running.
     */
    public enum MapState { Running, GameOver, GameWon }

    /**
     * Singleton reference to the Map to allow any object in the game to access the Map.
     */
    public static Map INSTANCE;
    /**
     * All of the objects on the map excluding any objects that can move.
     */
    private MapObject[][] mapObjects;
    /**
     * The player object that can be moved around to interact.
     */
    private Pacman pacman;
    /**
     * List of all the ghosts on the game. (assumed to be 4)
     */
    private List<Ghost> ghosts;
    /**
     * List of the cherries to be revealed after specific numbers of collected.
     * (assumed to be 2).
     */
    private List<CherryObject> cherries;
    /**
     * Number of grid cells horizontally.
     */
    private int mapWidth;
    /**
     * Number of grid cells vertically.
     */
    private int mapHeight;
    /**
     * Number of collectibles on the map.
     */
    private int totalCollectibles;
    /**
     * Number of collectibles collected so far.
     */
    private int collected;
    /**
     * State of the map. Defaults to Running.
     */
    private MapState mapState;
    /**
     * Current total score. Score of 1 for each collectible, 150 for cherries, and 100 for eating ghosts.
     */
    private int score;

    /**
     * Initialises the Map by creating the Singleton and then making it restart to load the map.
     */
    public Map() {
        INSTANCE = this;
        restart();
    }

    /**
     * Updates if in the running state by updating Pacman, and
     * then all of the ghost. Collision detection with the ghosts is checked.
     * Colliding with a ghost will either eat it to score 100 or result in a game over.
     */
    public void update() {
        if(mapState != MapState.Running) return;

        pacman.update();
        for(Ghost ghost : ghosts) {
            ghost.update();
            // Check for collision with Pacman
            if(pacman.getPosition().equals(ghost.getPosition())) {
                if(ghost.getGhostState() == Ghost.GhostState.Frightened) {
                    ghost.resetToStart();
                    addBonusScore(100);
                } else {
                    mapState = MapState.GameOver;
                }
            }
        }
    }

    /**
     * Draws all map objects, then draws Pacman and the ghosts on top.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paint(Graphics g) {
        for(int y = 0; y < mapHeight; y++) {
            for(int x = 0; x < mapWidth; x++) {
                mapObjects[x][y].paint(g);
            }
        }
        pacman.paint(g);
        for(Ghost ghost : ghosts) {
            ghost.paint(g);
        }
    }

    /**
     * Resets everything to do with the map and reloads using loadMap().
     */
    public void restart() {
        ghosts = new ArrayList<>();
        cherries = new ArrayList<>();
        loadMap();
        collected = 0;
        score = 0;
        mapState = MapState.Running;
    }

    /**
     * Gets a reference to Pacman, the player character.
     *
     * @return A reference to Pacman.
     */
    public Pacman getPacman() {
        return pacman;
    }

    /**
     * Tests if an object can move to a specified position based on the enter type of the target.
     *
     * @param positionToEnter The position to be tested for entry.
     * @param objectEntering The object attempting to enter the position (typically Pacman or a Ghost).
     * @param checkGhostCollisions Allows testing of entry based on Ghosts blocking the entry.
     * @return True if the object entering the position is allowed to move there.
     */
    public boolean canEnter(Position positionToEnter, MapObject objectEntering, boolean checkGhostCollisions) {
        // Test position is in the map bounds
        if(positionToEnter.x < 0 || positionToEnter.y < 0
                || positionToEnter.x >= mapWidth || positionToEnter.y >= mapHeight)
            return false;

        // Test if there is a Ghost already in that position
        if(checkGhostCollisions) {
            for(Ghost ghost : ghosts) {
                if(!ghost.equals(objectEntering) && ghost.position.equals(positionToEnter))
                    return false;
            }
        }

        // Test the entry requirements of the cell
        MapObject.EnterType enterType = mapObjects[positionToEnter.x][positionToEnter.y].enterType;
        return(enterType == MapObject.EnterType.Everyone
            || (enterType == MapObject.EnterType.PacmanOnly && objectEntering.objectType == MapObject.ObjectType.Pacman)
            || (enterType == MapObject.EnterType.GhostOnly && objectEntering.objectType == MapObject.ObjectType.Ghost));
    }

    /**
     * Called to use the onEnter event for the target cell. This allows triggers for collecting objects, and teleporting.
     *
     * @param objectThatMoved The object that has just entered a specified cell.
     */
    public void processEntering(MapObject objectThatMoved) {
        mapObjects[objectThatMoved.position.x][objectThatMoved.position.y].onEnter(objectThatMoved);
    }

    /**
     * Increases the number of collected objects along with the score.
     * The number of collected objects is used to inform activating of additional ghosts and cherries.
     * If all collectibles have been picked up the game state will change to won.
     */
    public void increaseCollected() {
        collected++;
        score++;
        if(collected == 1) ghosts.get(2).setState(Ghost.GhostState.Scatter);
        else if(collected == 30) ghosts.get(1).setState(Ghost.GhostState.Scatter);
        else if(collected == totalCollectibles/3) ghosts.get(3).setState(Ghost.GhostState.Scatter);

        if(collected == 70) cherries.get(0).reveal();
        else if(collected == 170) cherries.get(1).reveal();
        else if(collected == totalCollectibles) mapState = MapState.GameWon;
        //System.out.println("Collected " + collected + " of " + totalCollectibles);
    }

    /**
     * Adds a custom amount of score to the total score.
     *
     * @param bonus The amount of bonus score to apply.
     */
    public void addBonusScore(int bonus) {
        score += bonus;
    }

    /**
     * Iterates through all ghosts and tries to set their state to Frightened.
     * This will only work on ghosts that have become active.
     */
    public void frightenGhosts() {
        for(Ghost ghost : ghosts) {
            ghost.setState(Ghost.GhostState.Frightened);
        }
    }

    /**
     * Gets the current total score.
     *
     * @return The current score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Gets the map with all objects excluding ghosts and Pacman.
     *
     * @return All the map objects that make up the map grid.
     */
    public MapObject[][] getMapObjects() {
        return mapObjects;
    }

    /**
     * Gets the current map state. Indicates if the game is Running, GameWon, or GameOver.
     *
     * @return Current map state.
     */
    public MapState getMapState() {
        return mapState;
    }


    /**
     * Gets the specified ghost from the list.
     *
     * @param ghostID Position in the ghosts array.
     * @return Returns the ghost at the specified position in the list.
     */
    public Ghost getGhost(int ghostID) {
        return ghosts.get(ghostID);
    }

    /**
     * Populates all cells of the map by iterating through char data and mapping it to cells.
     *
     * @param mapData An array of Strings that include chars matching relevant objects.
     */
    private void populateMap(String[] mapData) {
        totalCollectibles = 0;
        mapWidth = mapData[0].length();
        mapHeight = mapData.length;
        mapObjects = new MapObject[mapWidth][mapHeight];
        for(int y = 0; y < mapHeight; y++) {
            for(int x = 0; x < mapWidth; x++) {
                mapObjects[x][y] = createObjectFromChar(mapData[y].charAt(x),new Position(x,y));
            }
        }
    }

    /**
     * Acts as an object factory. Returns either a special object or an empty map object.
     * Some objects require an additional object to be created. Ghosts are stored in the ghosts array,
     * and then an empty object is placed on the map. Same for Pacman with it instead stored in the
     * Pacman variable.
     *
     * @param c The character to indicate what should be created at this position.
     * @param position The position on the map.
     * @return The map object ready to be stored in the mapObjects array.
     */
    private MapObject createObjectFromChar(char c, Position position) {
        // create safe copy
        Position p = new Position(position);
        switch(c) {
            case 'W': return new WallObject(p);
            case '.': totalCollectibles++; return new CollectableObject(p);
            case '*': return new PowerupObject(p);
            case 'D': return new DoorObject(p);
            case 'T': return new TeleportObject(p,new Position(p.x==0?mapWidth-2:1,p.y),p.x==0?2:0);
            case 'G': ghosts.add(new Ghost(p, ghosts.size())); break;
            case 'P': pacman = new Pacman(p, this); break;
            case 'C' : CherryObject cherry = new CherryObject(p);
                        cherries.add(cherry); return cherry;
        }
        // Default to an empty object if it was none of the others
        return new MapObject(p, MapObject.ObjectType.Empty, MapObject.EnterType.Everyone);
    }

    /**
     * Attempt to load the map. First by loading from default.map.
     * If that fails, instead try to load from the data stored in an already existing array.
     */
    private void loadMap() {
        boolean success = loadFromFile("default.map");
        if(!success) {
            System.out.println("Failed to load from default.map.");
            System.out.println("Loading from backup data instead.");
            loadFromArray();
        }
    }

    /**
     * Loads from the already existing array and then sets up the GhostAI navMap.
     */
    private void loadFromArray() {
        populateMap(defaultMap);
        GhostAI.generateNavMap();
        GhostAI.applyNavMapRestrictions();
    }

    /**
     * Attempts to parse a map file and will fail if the format does not match.
     * First line must contain a width and height.
     * Then lines with correct lengths to match the width and height representing chars
     * for the map data with a ; at the end of each line.
     * Then in groups of 3 int values there can be any number of additional navMap restrictions specified.
     * With X Y Direction. Invalid restriction data will make it ignore any further input from the file,
     * and it will appear to load successfully.
     *
     * @param filename The file containing the map to load.
     * @return True if the file was loaded successfully.
     */
    private boolean loadFromFile(String filename) {
        Scanner scan;
        try {
            scan = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            exitFileLoadError("ERROR MAP FILE NOT FOUND!");
            return false;
        }
        if(!scan.hasNextInt()) {
            exitFileLoadError("ERROR INVALID FORMAT! Invalid Width!");
            return false;
        }
        int width = scan.nextInt();
        if(!scan.hasNextInt()) {
            exitFileLoadError("ERROR INVALID FORMAT! Invalid Height!");
            return false;
        }
        int height = scan.nextInt();
        scan.nextLine(); // Dump rest of line

        String[] mapData = new String[height];

        for(int y = 0; y < height; y++) {
            if(!scan.hasNextLine()) {
                exitFileLoadError("ERROR INVALID FORMAT! Missing lines of map.");
                return false;
            }
            mapData[y] = scan.nextLine();
            // The +1 is to include the ; at the end of the line.
            // The reason for this is to preserve empty spaces
            if(mapData[y].length() != width+1) {
                exitFileLoadError("ERROR INVALID FORMAT! Map line " + y + " not right size.");
                return false;
            }
        }

        populateMap(mapData);

        GhostAI.generateNavMap();
        // Apply navMap restrictions with X Y Direction
        while(scan.hasNextInt()) {
            int x = scan.nextInt();
            if(!scan.hasNextInt()) break;
            int y = scan.nextInt();
            if(!scan.hasNextInt()) break;
            int d = scan.nextInt();
            GhostAI.applyNavMapRestriction(x,y,d);
        }
        return true;
    }

    /**
     * Prints the error message.
     *
     * @param message The message to print.
     */
    private void exitFileLoadError(String message) {
        System.out.println(message);
        // You could uncomment the following line to exit when the error occurs.
        //System.exit(1);
    }

    /**
     * The fallback default map if the map file fails to load.
     */
    private final String[] defaultMap = {
            "WWWWWWWWWWWWWWWWWWWWWWWWWWWW",
            "W............WW............W",
            "W.WWWW.WWWWW.WW.WWWWW.WWWW.W",
            "W*W  W.W   W.WW.W   W.W  W*W",
            "W.WWWW.WWWWW.WW.WWWWW.WWWW.W",
            "W..........................W",
            "W.WWWW.WW.WWWWWWWW.WW.WWWW.W",
            "W.WWWW.WW.WWWWWWWW.WW.WWWW.W",
            "W......WW....WW....WW......W",
            "WWWWWW.WWWWW WW WWWWW.WWWWWW",
            "     W.WWWWW WW WWWWW.W     ",
            "     W.WW    CG    WW.W     ",
            "     W.WW WWWDDWWW WW.W     ",
            "WWWWWW.WW WWW  WWW WW.WWWWWW",
            "T     .   DG  G GD   .     T",
            "WWWWWW.WW WWWWWWWW WW.WWWWWW",
            "     W.WW WWWWWWWW WW.W     ",
            "     W.WW    C     WW.W     ",
            "     W.WW WWWWWWWW WW.W     ",
            "WWWWWW.WW WWWWWWWW WW.WWWWWW",
            "W............WW............W",
            "W.WWWW.WWWWW.WW.WWWWW.WWWW.W",
            "W.WWWW.WWWWW.WW.WWWWW.WWWW.W",
            "W*..WW....... P.......WW..*W",
            "WWW.WW.WW.WWWWWWWW.WW.WW.WWW",
            "WWW.WW.WW.WWWWWWWW.WW.WW.WWW",
            "W......WW....WW....WW......W",
            "W.WWWWWWWWWW.WW.WWWWWWWWWW.W",
            "W.WWWWWWWWWW.WW.WWWWWWWWWW.W",
            "W..........................W",
            "WWWWWWWWWWWWWWWWWWWWWWWWWWWW"};
}
