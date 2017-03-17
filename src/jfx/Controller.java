package jfx;

import engine.GameObject;
import engine.Level;
import engine.SokoEngine;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;

public class Controller {

    public MenuBar menu;
    public GridPane gameGrid;

    private Stage primaryStage;

    private SokoEngine sokoEngine;
    private Level currentLevel;

    /**
     * Loads the default game file.
     *
     * @param primaryStage the primary stage that will display the game
     */
    void loadDefaultSaveFile(Stage primaryStage) {
        this.primaryStage = primaryStage;
        File saveFile = new File(getSaveFileLocation() + "/SampleGame.skb");
        initializeGame(saveFile);
    }

    /**
     * Initializes the game using the provided game file.
     *
     * @param file the game file to be loaded
     */
    private void initializeGame(File file) {
        sokoEngine = new SokoEngine(file);
        currentLevel = sokoEngine.getCurrentLevel();
        setEventFilter();
        reloadGrid();
    }

    /**
     * Adds the event filter to handle {@link KeyEvent}s passing them to {@link SokoEngine}.
     */
    private void setEventFilter() {
        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            sokoEngine.handleKey(event.getCode());
            reloadGrid();
        });
    }

    /**
     * @return The folder where the save files are located
     */
    private File getSaveFileLocation() {
        File folder = new File(System.getProperty("user.dir") + "\\src\\level\\");

        if (!Files.exists(folder.toPath())) {
            folder = new File(System.getProperty("user.dir"));
        }

        return folder;
    }

    /**
     * Opens the load game window
     */
    private void loadGameFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Save File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Sokoban save file", "*.skb"));

        try {
            fileChooser.setInitialDirectory(getSaveFileLocation());
        } finally {
            File file = fileChooser.showOpenDialog(primaryStage);

            if (file != null)
                initializeGame(file);
        }
    }

    /**
     * Reloads the grid using the {@link Level} iterator.
     */
    private void reloadGrid() {
        Level.LevelIterator levelGridIterator = (Level.LevelIterator) currentLevel.iterator();

        while (levelGridIterator.hasNext()) {
            addObjectToGrid(levelGridIterator.next(), levelGridIterator.getCurrentPosition());
        }

        gameGrid.autosize();
        primaryStage.sizeToScene();
    }

    /**
     * Adds an object to the specified grid position.
     * It first converts a {@link GameObject} into a {@link javafx.scene.shape.Rectangle},
     * then it adds the new rectangle into the specified location.
     *
     * @param gameObject The game object to be added into the grid
     * @param location   the location where the game object will be added
     */
    private void addObjectToGrid(GameObject gameObject, Point location) {
        GraphicObject graphicObject = new GraphicObject(gameObject);
        gameGrid.add(graphicObject, location.y, location.x);
    }

    public void closeGame(ActionEvent actionEvent) {
        // TODO
    }

    public void saveGame(ActionEvent actionEvent) {
        // TODO
    }

    public void loadGame(ActionEvent actionEvent) {
        // TODO
    }

    public void undo(ActionEvent actionEvent) {
        // TODO
    }

    public void resetLevel(ActionEvent actionEvent) {
        // TODO
    }
}
