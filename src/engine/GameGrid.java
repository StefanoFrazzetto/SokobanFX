package engine;

import java.awt.*;
import java.util.Iterator;

/**
 * GameGrid class can be used to create a 2D grid and gameObjects to it.
 */
public class GameGrid implements Iterable {

    /**
     * The number of columns
     */
    final int COLUMNS;
    /**
     * The number of rows
     */
    final int ROWS;

    /**
     * The grid containing every GameObject
     */
    private GameObject[][] gameObjects;

    /**
     * Creates the grid using columns and rows to set the maximum size.
     *
     * @param columns the number of columns
     * @param rows    the number of rows
     */
    public GameGrid(int columns, int rows) {
        COLUMNS = columns;
        ROWS = rows;

        // Initialize the array
        gameObjects = new GameObject[COLUMNS][ROWS];
    }

    /**
     * Returns the point located at a distance delta from a starting GameObject.
     *
     * @param sourceLocation the source point
     * @param delta          the distance between the reference GameObject and the target point
     * @return the target point at a distance delta from sourceLocation.
     */
    static Point translatePoint(Point sourceLocation, Point delta) {
        Point translatedPoint = new Point(sourceLocation);
        translatedPoint.translate((int) delta.getX(), (int) delta.getY());
        return translatedPoint;
    }

    /**
     * Returns the size of this grid as {@link Dimension}.
     *
     * @return a {@link Dimension} GameObject that indicates the size of this grid
     */
    public Dimension getDimension() {
        return new Dimension(COLUMNS, ROWS);
    }

    /**
     * Gets the GameObject at delta distance from the origin GameObject.
     *
     * @param source the source GameObject location
     * @param delta  the distance from source
     * @return the target GameObject
     */
    GameObject getTargetFromSource(Point source, Point delta) {
        return getGameObjectAt(translatePoint(source, delta));
    }

    /**
     * Gets the GameObject positioned at (x, y).
     *
     * @param col the row of the GameObject
     * @param row the column of the GameObject
     * @return GameObject
     * @throws ArrayIndexOutOfBoundsException if the coordinates are outside the grid bounds
     */
    public GameObject getGameObjectAt(int col, int row) throws ArrayIndexOutOfBoundsException {
        if (isPointOutOfBounds(col, row)) {
            if (GameEngine.isDebugActive()) {
                System.out.printf("Trying to get null GameObject from COL: %d  ROW: %d", col, row);
            }
            throw new ArrayIndexOutOfBoundsException("The point [" + col + ":" + row + "] is outside the map.");
        }

        return gameObjects[col][row];
    }

    /**
     * Gets an GameObject located at the chosen {@link Point}
     *
     * @param p the point where the GameObject should be found
     * @return GameObject if the the GameObject is found || null if the point is null
     */
    public GameObject getGameObjectAt(Point p) {
        if (p == null) {
            throw new IllegalArgumentException("Point cannot be null.");
        }

        return getGameObjectAt((int) p.getX(), (int) p.getY());
    }

    /**
     * Removes a {@link GameObject} from the grid
     *
     * @param position the position putting null in the GameObject position
     * @return boolean  true if it was possible to remove the GameObject, false otherwise
     */
    public boolean removeGameObjectAt(Point position) {
        return putGameObjectAt(null, position);
    }


    /**
     * Puts a {@link GameObject} into the specified location (x, y).
     *
     * @param gameObject the gameObject to be put into the array
     * @param x          the x coordinate
     * @param y          the y coordinate
     * @return true if the operation is successful, false otherwise
     */
    public boolean putGameObjectAt(GameObject gameObject, int x, int y) {
        if (isPointOutOfBounds(x, y)) {
            return false;
        }

        gameObjects[x][y] = gameObject;
        return gameObjects[x][y] == gameObject;
    }

    /**
     * Puts a {@link GameObject} into the specified point.
     *
     * @param gameObject the GameObject to be put into the array
     * @param p          the point where the GameObject will be put into
     * @return true if the operation is successful, false otherwise.
     */
    public boolean putGameObjectAt(GameObject gameObject, Point p) {
        return p != null && putGameObjectAt(gameObject, (int) p.getX(), (int) p.getY());
    }

    /**
     * Checks if a point is outside the grid.
     *
     * @param x the x position on the grid
     * @param y the y position on the grid
     * @return true if the point is outside the grid, false otherwise.
     */
    private boolean isPointOutOfBounds(int x, int y) {
        return (x < 0 || y < 0 || x >= COLUMNS || y >= ROWS);
    }

    /**
     * Checks if a point is outside the grid.
     *
     * @param p the point to be checked
     * @return true if the point is outside the grid, false otherwise.
     */
    private boolean isPointOutOfBounds(Point p) {
        return isPointOutOfBounds(p.x, p.y);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(gameObjects.length);

        for (GameObject[] gameObject : gameObjects) {
            for (GameObject aGameObject : gameObject) {
                if (aGameObject == null) {
                    aGameObject = GameObject.DEBUG_OBJECT;
                }
                sb.append(aGameObject.getCharSymbol());
            }

            sb.append('\n');
        }

        return sb.toString();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<GameObject> iterator() {
        return new GridIterator();
    }

    /**
     * GridIterator provides the interface to iterate through the grid containing
     * the {@link GameObject}s for the current {@link Level}.
     *
     * @see Iterator
     */
    public class GridIterator implements Iterator<GameObject> {
        int row = 0;
        int column = 0;

        @Override
        public boolean hasNext() {
            return !(row == ROWS && column == COLUMNS);
        }

        @Override
        public GameObject next() {
            if (column >= COLUMNS) {
                column = 0;
                row++;
            }
            return getGameObjectAt(column++, row);
        }
    }
}