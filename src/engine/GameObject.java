package engine;

/**
 * Created by stefa on 26/10/2016.
 *
 */
public enum GameObject {
    WALL('W'),
    FLOOR(' '),
    CRATE('C'),
    DIAMOND('D'),
    KEEPER('S');

    private final char symbol;

    GameObject(final char symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the string representation of the symbol.
     *
     * @return String
     */
    public String getStringSymbol() {
        return String.valueOf(symbol);
    }

    public char getCharSymbol() {
        return symbol;
    }

    /**
     * Checks if c is a tile.
     * If the char is not associated with any enum, it will return FLOOR as default value.
     *
     * @param c - the char to look for
     * @return the {@link GameObject} corresponding to the char
     */
    public static GameObject fromChar(char c) {
        for (GameObject t : GameObject.values()) {
            if (Character.toUpperCase(c) == t.symbol) {
                return t;
            }
        }

        // Not recognized char was passed. It will be a floor.
        return WALL;
    }
}