package engine;

import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.sound.sampled.LineUnavailableException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * GameEngine is responsible for handling all the game mechanics.
 *
 * @author Stefano Frazzetto
 * @version 2.0.0
 */
public class GameEngine {

    /**
     * The repository URL
     */
    public final static String GitHubURL = "https://github.com/StefanoFrazzetto/NewSokobanFX";
    /**
     * The game name showed in the game dialog
     */
    public static final String GAME_NAME = "SokobanFX by Stefano Frazzetto";
    /**
     * The system logger
     */
    public static GameLogger logger;
    /**
     * The game debug mode
     */
    private static boolean debug = false;
    /**
     * The current level displayed in the game
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
     * The game state
     */
    private boolean gameComplete = false;
    /**
     * The number of moves
     */
    private int movesCount = 0;

    /**
     * The music player
     */
    private MediaPlayer player;

    /**
     * Uses a {@link File} to load the game map containing all the levels.
     *
     * @param file the file containing the game levels.
     * @param production true if using the engine in live mode, false
     *                   only for testing mode.
     */
    public GameEngine(File file, boolean production) {
        try {
            // Initialize the logger
            logger = new GameLogger();
            levels = loadGameFile(file);
            currentLevel = getNextLevel();

            if (production) {
                createPlayer();
            }
        } catch (IOException x) {
            System.out.println("Cannot create logger.");
        } catch (NoSuchElementException e) {
            logger.warning("Cannot load the default save file: " + e.getStackTrace());
        } catch (LineUnavailableException e) {
            logger.warning("Cannot load the music file: " + e.getStackTrace());
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

    public int getMovesCount() {
        return movesCount;
    }

    public String getMapSetName() {
        return mapSetName;
    }

    /**
     * Handles the action that should be executed when a specific
     * keyboard key {@link KeyCode} is pressed.
     *
     * @param code the keyboard key code.
     */
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
                // TODO: implement something funny.
        }

        if (isDebugActive()) {
            System.out.println(code);
        }
    }

    /**
     * Handles the movement of the keeper and the objects that collide with it
     *
     * @param delta - the movement delta
     */
    private void move(Point delta) {
        // Prevent the player from moving if the game is complete.
        if (isGameComplete()) {
            return;
        }

        Point keeperPosition = currentLevel.getKeeperPosition();
        // Check what kind of object is located at target
        GameObject keeper = currentLevel.getObjectAt(keeperPosition);
        Point targetObjectPoint = GameGrid.translatePoint(keeperPosition, delta);
        GameObject keeperTarget = currentLevel.getObjectAt(targetObjectPoint);

        // Print useful information if the debug mode is active.
        if (GameEngine.isDebugActive()) {
            System.out.println("Current level state:");
            System.out.println(currentLevel.toString());
            System.out.println("Keeper pos: " + keeperPosition);
            System.out.println("Movement source obj: " + keeper);
            System.out.printf("Target object: %s at [%s]", keeperTarget, targetObjectPoint);
        }

        boolean keeperMoved = false;

        // Check keeper target
        switch (keeperTarget) {

            case WALL:
                // Cannot move
                break;

            case CRATE:

                GameObject crateTarget = currentLevel.getTargetObject(targetObjectPoint, delta);
                // If the crate target is not FLOOR, cannot move.
                if (crateTarget != GameObject.FLOOR) {
                    break;
                }

                currentLevel.moveGameObjectBy(keeperTarget, targetObjectPoint, delta);
                currentLevel.moveGameObjectBy(keeper, keeperPosition, delta);
                keeperMoved = true;
                break;

            case FLOOR:
                currentLevel.moveGameObjectBy(keeper, keeperPosition, delta);
                keeperMoved = true;
                break;

            default:
                logger.severe("The object to be moved was not a recognised GameObject.");
                throw new AssertionError("This should not have happened. Report this problem to the developer.");
        }

        if (keeperMoved) {
            keeperPosition.translate((int) delta.getX(), (int) delta.getY());
            movesCount++;
            if (currentLevel.isComplete()) {
                if (isDebugActive()) {
                    System.out.println("Level complete!");
                }

                currentLevel = getNextLevel();
            }
        }
    }

    /**
     * Loads a game file creating a {@link List} of {@link Level}s.
     *
     * @param file - the file containing the levels
     * @return the list containing the levels
     */
    private List<Level> loadGameFile(File file) {
        List<Level> levels = new ArrayList<>(5);
        int levelIndex = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
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
                // If the line contains at least 2 WALLS, add it to the list
                if (line.matches(".*W.*W.*")) {
                    rawLevel.add(line);
                }
            } // END - While loop

        } catch (IOException e) {
            logger.severe("Error trying to load the game file: " + e);
        }

        return levels;
    }

    /**
     * Returns true if the game is complete.
     *
     * @return true if the game is complete, false otherwise
     */
    public boolean isGameComplete() {
        return gameComplete;
    }

    /**
     * Creates the player object loading the music file.
     *
     * @throws LineUnavailableException if the file is not available.
     */
    private void createPlayer() throws LineUnavailableException {
        File filePath = new File(System.getProperty("user.dir") + "/src/music/puzzle_theme.wav");
        Media music = new Media(filePath.toURI().toString());
        player = new MediaPlayer(music);
        player.setOnEndOfMedia(() -> player.seek(Duration.ZERO));
    }

    /**
     * Starts playing music.
     */
    public void playMusic() {
        player.play();
    }

    /**
     * Stops playing music.
     */
    public void stopMusic() {
        player.stop();
    }

    /**
     * Returns true if the player is playing music.
     *
     * @return true if playing music, false otherwise.
     */
    public boolean isPlayingMusic() {
        return player.getStatus() == MediaPlayer.Status.PLAYING;
    }

    /**
     * Returns the next level in the list of levels.
     *
     * @return the next level loaded from the save file.
     */
    private Level getNextLevel() {
        if (currentLevel == null) {
            return levels.get(0);
        }

        int currentLevelIndex = currentLevel.getIndex();
        if (currentLevelIndex < levels.size()) {
            return levels.get(currentLevelIndex + 1);
        }

        gameComplete = true;
        return null;
    }

    /**
     * Returns the current level.
     *
     * @return the current level.
     */
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