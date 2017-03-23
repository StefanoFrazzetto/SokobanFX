package tests;

import engine.GameGrid;
import engine.GameObject;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GameGridTests {

    private static GameGrid grid;
    private int rows = 5;
    private int columns = 5;

    @Before
    public void testConstructor() {
        grid = new GameGrid(columns, rows);
    }

    @Test
    public void testPutObjectAt1() {
        int nr = rows + 1;
        int nc = columns + 1;
        assertFalse(grid.putGameObjectAt(GameObject.CRATE, nr, nc));
    }

    @Test
    public void testPutObjectAt2() {
        int nr = rows - 1;
        int nc = columns - 1;
        assertTrue(grid.putGameObjectAt(GameObject.CRATE, nr, nc));
    }

    @Test
    public void testPutObjectAt3() {
        int nr = rows;
        int nc = columns;
        assertFalse(grid.putGameObjectAt(GameObject.CRATE, nr, nc));
    }

    @Test
    public void testPutObjectAt4() {
        grid.putGameObjectAt(GameObject.CRATE, 0,0);
        assertTrue(grid.getGameObjectAt(0,0) == GameObject.CRATE);
    }

    @Test
    public void testDimension() {
        assertTrue("The grid dimension is wrong", grid.getDimension().equals(new Dimension(columns, rows)));
    }
}
