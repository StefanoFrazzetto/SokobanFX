package jfx;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import engine.GameEngine;
import engine.GameObject;

/**
 * GraphicObject is used to populate the game grid.
 * It creates {@link Rectangle} from {@link GameObject}.
 */
class GraphicObject extends Rectangle {

    GraphicObject(GameObject obj) {
        Paint color;
        switch (obj) {
            case WALL:
                color = Color.BLACK;
                break;

            case CRATE:
                color = Color.ORANGE;
                break;

            case DIAMOND:
                color = Color.DEEPSKYBLUE;

                // TODO: fix memory leak.
                if (GameEngine.isDebugActive()) {
                    FadeTransition ft = new FadeTransition(Duration.millis(1000), this);
                    ft.setFromValue(1.0);
                    ft.setToValue(0.2);
                    ft.setCycleCount(Timeline.INDEFINITE);
                    ft.setAutoReverse(true);
                    ft.play();
                }

                break;

            case KEEPER:
                color = Color.RED;
                break;

            case FLOOR:
                color = Color.WHITE;
                break;

            case CRATE_ON_DIAMOND:
                color = Color.DARKCYAN;
                break;

            default:
                String message = "Error in Level constructor. Object not recognized.";
                GameEngine.logger.severe(message);
                throw new AssertionError(message);
        }

        this.setFill(color);
        this.setHeight(30);
        this.setWidth(30);

        if (obj != GameObject.WALL) {
            this.setArcHeight(50);
            this.setArcWidth(50);
        }

        if (GameEngine.isDebugActive()) {
            this.setStroke(Color.RED);
            this.setStrokeWidth(0.25);
        }
    }

}
