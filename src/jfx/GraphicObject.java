package jfx;

import engine.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

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
                SokoEngine.logger.severe(message);
                throw new AssertionError(message);
        }

        if (SokoEngine.isDebugActive()) {
            this.setStroke(Color.RED);
        }

        this.setFill(color);
        this.setHeight(30);
        this.setWidth(30);
    }

}
