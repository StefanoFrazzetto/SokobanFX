package tests;

import engine.GameObject;
import engine.Level;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static junit.framework.TestCase.assertTrue;

public class LevelTests {

    private Method getKeeperPosition;
    private Method getObjectAt;
    private Level level;

    @Before
    public void testConstructor() {
        level = GameEngineTests.level;
        assertTrue(level != null);
    }

    @Test
    public void testGetKeeperPosition() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Point check = new Point(1,1);
        getKeeperPosition = Level.class.getDeclaredMethod("getKeeperPosition", null);
        getKeeperPosition.setAccessible(true);

        Point keeper = (Point) getKeeperPosition.invoke(level, null);

        assertTrue("Keeper position" + keeper + ", expected: " + check, keeper.equals(check));
    }

    @Test
    public void testGetObjectAt() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        getObjectAt = Level.class.getDeclaredMethod("getObjectAt", Point.class);
        getObjectAt.setAccessible(true);

        GameObject go = (GameObject) getObjectAt.invoke(level, new Point(0,0));

        assertTrue("GameObjects are not equal", go == GameObject.WALL);
    }
}
