package engine;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

import static engine.GameGrid.translatePoint;


/**
 * Level handles the creation of the game level parsing a {@link List} of {@link String}s and putting the right
 * {@link GameObject} in a 2D array. The object is created matching a char with the corresponding {@link GameObject}.
 *
 * @author Stefano Frazzetto
 * @since 2.3.0
 */
public final class Level implements Iterable<GameObject> {

    /**
     * The level name
     */
    private final String name;
    /**
     * The array containing the objectsGrid
     */
    private final GameGrid objectsGrid;
    /**
     * The object containing the diamondsGrid.
     */
    private final GameGrid diamondsGrid;
    /**
     * The level index
     */
    private final int index;
    /**
     * The total number of diamondsGrid in this level
     */
    private int numberOfDiamonds = 0;
    /**
     * The current warehouse keeper position
     */
    private Point keeperPosition = new Point(0, 0);

    /**
     * Creates a level using the first parameter as the level name and the second parameter as {@link List} of
     * {@link String}, each one containing the characters corresponding to a specific game object
     *
     * @param levelName  the name of the level
     * @param levelIndex the number used as index for the levels
     * @param raw_level  the raw data of the level
     */
    public Level(String levelName, int levelIndex, List<String> raw_level) {
        if (SokoEngine.isDebugActive()) {
            System.out.printf("[ADDING LEVEL] LEVEL [%d]: %s\n", levelIndex, levelName);
        }

        name = levelName;
        index = levelIndex;

        int rows = raw_level.size();
        // Get the first row, trim it to remove any space before and after it, then get its length.
        int columns = raw_level.get(0).trim().length();

        // Create the grids grids
        objectsGrid = new GameGrid(rows, columns);
        diamondsGrid = new GameGrid(rows, columns);

        // Loop over the List
        for (int row = 0; row < raw_level.size(); row++) {

            // Loop over the string one char at a time because it should be the fastest way:
            // http://stackoverflow.com/questions/8894258/fastest-way-to-iterate-over-all-the-chars-in-a-string
            for (int col = 0; col < raw_level.get(row).length(); col++) {
                // The game object is null when the we're adding a floor or a diamond
                GameObject curTile = GameObject.fromChar(raw_level.get(row).charAt(col));

                // If the tile is a diamond, add it to the diamond grid and add a floor
                // into the objects grid.
                if (curTile == GameObject.DIAMOND) {
                    numberOfDiamonds++;
                    diamondsGrid.putGameObjectAt(curTile, row, col);
                    curTile = GameObject.FLOOR;
                } else if (curTile == GameObject.KEEPER) {
                    keeperPosition = new Point(row, col);
                }

                objectsGrid.putGameObjectAt(curTile, row, col);
                curTile = null;
            } // END- String loop
        } // END - List loop
    }

    boolean isComplete() {
        int cratedDiamondsCount = 0;
        for (int row = 0; row < objectsGrid.ROWS; row++) {
            for (int col = 0; col < objectsGrid.COLUMNS; col++) {
                if (objectsGrid.getGameObjectAt(col, row) == GameObject.CRATE && diamondsGrid.getGameObjectAt(col, row) == GameObject.DIAMOND) {
                    cratedDiamondsCount++;
                }
            }
        }

        return cratedDiamondsCount >= numberOfDiamonds;
    }

    /**
     * Returns the name of this level
     *
     * @return the name of this level
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the level index
     *
     * @return the level index
     */
    int getIndex() {
        return index;
    }

    /**
     * Returns the warehouse keeper position
     *
     * @return the warehouse keeper position
     */
    Point getKeeperPosition() {
        return keeperPosition;
    }

    /**
     * Returns the object at distance delta from source
     *
     * @param source the source point
     * @param delta  the distance from the source point
     * @return the object at distance delta from source
     */
    GameObject getTargetObject(Point source, Point delta) {
        return objectsGrid.getTargetFromSource(source, delta);
    }

    /**
     * Returns located at point p of the objects grid.
     *
     * @param p the point where is object is located
     * @return GameObject the objected located at point p.
     */
    GameObject getObjectAt(Point p) {
        return objectsGrid.getGameObjectAt(p);
    }

    void moveGameObjectBy(GameObject object, Point source, Point delta) {
        moveGameObjectTo(object, source, translatePoint(source, delta));
    }

    /**
     * Moves a {@link GameObject} to the target destination.
     * It removes the object from its original position and places it into the new one.
     *
     * @param object      - the {@link GameObject} to be moved
     * @param source      - the position of the object to be moved
     * @param destination - the "Final Destination" for the object (pun intended)
     */
    private void moveGameObjectTo(GameObject object, Point source, Point destination) {
        objectsGrid.putGameObjectAt(getObjectAt(destination), source);
        objectsGrid.putGameObjectAt(object, destination);
    }

    @Override
    public String toString() {
        return objectsGrid.toString();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<GameObject> iterator() {
        return new LevelIterator();
    }


    /**
     * LevelIterator provides the interface to iterate through the {@link GameGrid}
     * containing the {@link GameObject}s for the current {@link Level}.
     *
     * @see Iterator
     */
    public class LevelIterator implements Iterator<GameObject> {

        int column = 0;
        int row = 0;

        @Override
        public boolean hasNext() {
            return !(row == objectsGrid.ROWS - 1 && column == objectsGrid.COLUMNS);
        }

        @Override
        public GameObject next() {
            if (column >= objectsGrid.COLUMNS) {
                column = 0;
                row++;
            }

            GameObject object = objectsGrid.getGameObjectAt(column, row);
            GameObject diamond = diamondsGrid.getGameObjectAt(column, row);
            GameObject retObj = object;

            // After the object is assigned, increment the column number.
            column++;

            // If the diamonds grid contains a diamond, then return it.
            if (diamond == GameObject.DIAMOND) {
                if (object == GameObject.CRATE) {
                    retObj = GameObject.CRATE_ON_DIAMOND;
                } else if (object == GameObject.FLOOR) {
                    retObj = diamond;
                } else {
                    retObj = object;
                }
            }

            return retObj;
        }

        public Point getCurrentPosition() {
            return new Point(column, row);
        }
    }
}