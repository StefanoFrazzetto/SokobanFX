package jfx;

import engine.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * GraphicObject is used to create a {@link Rectangle} from a {@link GameObject}.
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
                color = Color.DARKCYAN;
                break;

            case KEEPER:
                color = Color.RED;
                break;

            case FLOOR:
                color = Color.WHITE;
                break;

            default:
                SokoEngine.logger.severe("Error in Level constructor. Object not recognized.");
                throw new AssertionError("Something impossible just happened in Level constructor.");
        }

        if (SokoEngine.isDebugActive()) {
            this.setStroke(Color.RED);
        }

        this.setFill(color);
        this.setHeight(30);
        this.setWidth(30);
    }

}
