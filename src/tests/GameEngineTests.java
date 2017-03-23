package tests;

import engine.Level;
import engine.GameEngine;
import javafx.application.Application;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class GameEngineTests extends Application {

    public static GameEngine engine;
    public static Level level;

    private Method move;

    @Before
    public void setUp() throws Exception {
        File saveFile = new File(this.getClass().getResource("../level/debugLevel.skb").toURI());
        System.out.println(saveFile);

        engine = new GameEngine(saveFile, false);
        level = engine.getCurrentLevel();
    }

    @Test
    public void testConstructor() {
        assertTrue("Engine has not been initialized", engine != null);
    }

    @Test
    public void testGetNextLevel() {
        assertTrue("The levels are not equal", engine.getCurrentLevel().equals(level));
    }

    @Test
    public void testIsGameNOTComplete() {
        assertFalse("The level should not be complete", engine.isGameComplete());
    }

    @Test
    public void testHandleKey() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        engine.handleKey(KeyCode.RIGHT);
        assertTrue("The keeper has not moved", engine.getMovesCount() == 1);
    }

    @Test
    public void testMove() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        move = GameEngine.class.getDeclaredMethod("move", Point.class);
        move.setAccessible(true);

        move.invoke(engine, new Point(0, 1));
        assertTrue("The keeper has not moved", engine.getMovesCount() > 0 );
    }

    @Test
    public void testLevelComplete() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.testMove();
        this.testMove();
        assertTrue("The game is not complete", engine.isGameComplete());
    }

    @BeforeClass
    public static void initJFX() {
        Thread t = new Thread("JavaFX Init Thread") {
            public void run() {
                Application.launch(GameEngineTests.class, new String[0]);
            }
        };
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}
