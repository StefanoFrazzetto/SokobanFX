package engine;

import javafx.scene.input.KeyCode;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * SokoEngine is responsible for handling all the game mechanics.
 *
 * @author Stefano Frazzetto
 * @version 1.0.0
 */
public class SokoEngine {

    public static final String GAME_NAME = "Sokoban";

    /**
     * The system logger
     */
    public static SokoLogger logger;
    /**
     * The game debug mode
     */
    private static boolean debug = true;
    /**
     * The current level displayed in the game grid
     */
    private Level currentLevel;

    /**
     * The map set name
     */
    private String mapSetName;
    /**
     * The list of levels
     */
    private List<Level> levels;
    /**
     * The WarehouseKeeper position
     */
    private Point keeperPosition;

    /**
     * Uses a {@link File} to load the game map containing all the levels.
     *
     * @param file
     */
    public SokoEngine(File file) {
        try {
            // Initialize the logger
            logger = new SokoLogger();
            levels = loadGameFile(file);
            currentLevel = getNextLevel();

            assert currentLevel != null;
            keeperPosition = currentLevel.getKeeperPosition();
        } catch (IOException x) {
            System.out.println("Cannot create logger.");
        } catch (NoSuchElementException e) {
            System.out.println("Cannot load default save file");
        }
    }

    /**
     * Checks if the debug mode is active.
     *
     * @return boolean true if the debug mode is active, false otherwise
     */
    public static boolean isDebugActive() {
        return debug;
    }

    public void handleKey(KeyCode code) {
        switch (code) {
            case UP:
                move(new Point(-1, 0));
                break;

            case RIGHT:
                move(new Point(0, 1));
                break;

            case DOWN:
                move(new Point(1, 0));
                break;

            case LEFT:
                move(new Point(0, -1));
                break;

            default:
        }

        System.out.println(code);
    }

    /**
     * Handles the movement of the keeper and the objects that collide with it
     *
     * @param delta - the movement delta
     */
    public void move(Point delta) {
        // Check what kind of object is located at target
        GameObject sourceObject = currentLevel.getObjectAt(keeperPosition);
        Point targetObjectPoint = GameGrid.translatePoint(keeperPosition, delta);
        GameObject targetObject = currentLevel.getObjectAt(targetObjectPoint);

        // Print useful information if the debug mode is active.
        if (SokoEngine.isDebugActive()) {
            System.out.println("Current level state:");
            System.out.println(currentLevel.toString());
            System.out.println("Keeper pos: " + keeperPosition);
            System.out.println("Movement source obj: " + sourceObject);
            System.out.printf("Target object: %s at [%s]", targetObject, targetObjectPoint);
        }

        boolean keeperMoved = false;

        switch (targetObject) {

            case WALL:
                // Cannot move
                break;

            case CRATE:

                // Prevent a Crate from moving against a non FLOOR object.
                if (currentLevel.getTargetObject(targetObjectPoint, delta) != GameObject.FLOOR) {
                    break;
                }

                currentLevel.moveGameObjectBy(targetObject, targetObjectPoint, delta);
                currentLevel.moveGameObjectBy(sourceObject, keeperPosition, delta);
                keeperMoved = true;

                break;

            case DIAMOND:
                keeperMoved = true;
                break;

            case FLOOR:
                currentLevel.moveGameObjectBy(sourceObject, keeperPosition, delta);
                keeperMoved = true;
                break;

            default:
                logger.severe("The object to be moved was not a recognised GameObject.");
                throw new AssertionError("This should not have happened. Report this problem to the developer.");
        }

        if (keeperMoved) {
            keeperPosition.translate((int) delta.getX(), (int) delta.getY());
        }
    }

    /**
     * Loads a game file creating a List of {@link Level}.
     *
     * @param file - the file containing the levels
     * @return the list containing the levels
     */
    private List<Level> loadGameFile(File file) {
        List<Level> levels = new ArrayList<>(5);
        int levelIndex = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String mapSetName = "";
            boolean parsedFirstLevel = false;
            List<String> rawLevel = new ArrayList<>();
            String levelName = "";

            // START - While loop
            while (true) {
                String line = reader.readLine();

                // Break the loop if EOF is reached
                if (line == null) {
                    // If the list size is not equal to zero, it means
                    // that the last level has not been parsed yet.
                    if (rawLevel.size() != 0) {
                        Level parsedLevel = new Level(levelName, ++levelIndex, rawLevel);
                        levels.add(parsedLevel);
                    }
                    break;
                }

                // If the line contains the map set name, save it
                if (line.contains("MapSetName")) {
                    mapSetName = line.replace("MapSetName: ", "");
                    continue;
                }

                // If the line contains the level name, save it
                if (line.contains("LevelName")) {
                    // If we already parsed the first level, it means that this is the n+1 level,
                    // so we can already parse it.
                    if (parsedFirstLevel) {
                        Level parsedLevel = new Level(levelName, ++levelIndex, rawLevel);
                        levels.add(parsedLevel);
                        rawLevel.clear();
                    } else {
                        parsedFirstLevel = true;
                    }

                    // Get the level name by removing "LevelName:"
                    levelName = line.replace("LevelName: ", "");
                    continue;
                }

                line = line.trim();
                line = line.toUpperCase();
                // If the line contains a level's part, add it to the list
                if (line.matches(".*W.*W.*")) {
                    rawLevel.add(line);
                }
            } // END - While loop

        } catch (IOException e) {
            logger.severe("Error trying to load the game file: " + e);
        }

        return levels;
    }

    private Level getNextLevel() {
        if (currentLevel == null) {
            return levels.get(0);
        }

        int currentLevelIndex = currentLevel.getIndex();
        if (currentLevelIndex < levels.size()) {
            return levels.get(currentLevelIndex + 1);
        } else {
            return null;
        }
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Toggles the debug mode.
     */
    public void toggleDebug() {
        debug = !debug;
    }

}