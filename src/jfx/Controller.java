package jfx;

import engine.GameObject;
import engine.Level;
import engine.SokoEngine;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.effect.Effect;
import javafx.scene.effect.MotionBlur;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;

public class Controller {

    public MenuBar menu;
    public GridPane gameGrid;
    private Stage primaryStage;
    private SokoEngine sokoEngine;
    private File saveFile;

    /**
     * Loads the default game file.
     *
     * @param primaryStage the primary stage that will display the game
     */
    void loadDefaultSaveFile(Stage primaryStage) {
        this.primaryStage = primaryStage;
        saveFile = new File(getSaveFileLocation() + "/SampleGame.skb");
        setEventFilter();
        initializeGame(saveFile);
    }

    /**
     * Initializes the game using the provided game file.
     *
     * @param file the game file to be loaded
     */
    private void initializeGame(File file) {
        sokoEngine = new SokoEngine(file);
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

        fileChooser.setInitialDirectory(getSaveFileLocation());
        File saveFile = fileChooser.showOpenDialog(primaryStage);

        if (saveFile != null) {
            if (SokoEngine.isDebugActive()) {
                SokoEngine.logger.info("Loading save file: " + saveFile.getName());
            }
            initializeGame(saveFile);
        }

    }

    /**
     * Reloads the grid using the {@link Level} iterator.
     */
    private void reloadGrid() {
        if (sokoEngine.isGameComplete()) {
            // TODO: fix last move of the game.
            showVictoryMessage();
            return;
        }

        Level currentLevel = sokoEngine.getCurrentLevel();
        Level.LevelIterator levelGridIterator = (Level.LevelIterator) currentLevel.iterator();

        while (levelGridIterator.hasNext()) {
            addObjectToGrid(levelGridIterator.next(), levelGridIterator.getCurrentPosition());
        }

        gameGrid.autosize();
        primaryStage.sizeToScene();
    }

    private void showVictoryMessage() {
        String dialogTitle = "Game Over!";
        String dialogMessage = "You completed " + sokoEngine.getMapSetName() + " in " + sokoEngine.getMovesCount() + " moves!";
        MotionBlur mb = new MotionBlur(2, 3);

        newDialog(dialogTitle, dialogMessage, mb);
    }

    private void newDialog(String dialogTitle, String dialogMessage, Effect dialogMessageEffect) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setResizable(false);
        dialog.setTitle(dialogTitle);

        Text text1 = new Text(dialogMessage);
        text1.setTextAlignment(TextAlignment.CENTER);
        text1.setFont(javafx.scene.text.Font.font(14));

        if (dialogMessageEffect != null) {
            text1.setEffect(dialogMessageEffect);
        }

        VBox dialogVbox = new VBox(20);
        dialogVbox.setAlignment(Pos.CENTER);
        dialogVbox.setBackground(Background.EMPTY);
        dialogVbox.getChildren().add(text1);

        Scene dialogScene = new Scene(dialogVbox, 350, 150);
        dialog.setScene(dialogScene);
        dialog.show();
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
        loadGameFile();
    }

    public void undo(ActionEvent actionEvent) {
        // TODO
    }

    public void resetLevel(ActionEvent actionEvent) {
        initializeGame(saveFile);
    }

    public void showAbout(ActionEvent actionEvent) {
        String title = "About this game";
        String message = "Game created by Stefano Frazzetto\n\nContribute on GitHub:\n" + SokoEngine.GitHubURL;

        newDialog(title, message, null);
    }
}
