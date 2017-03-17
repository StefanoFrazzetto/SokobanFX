package engine;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Handles the game logging.
 * The logfile is placed in the directory where the game is executed under a directory named "GAME_NAME"-logfiles.
 */
public class SokoLogger extends Logger {

    private static Logger logger = Logger.getLogger("GameLogger");
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private Calendar calendar = Calendar.getInstance();

    public SokoLogger() throws IOException {
        super("com.stefanofrazzetto.sokoban", null);

        File directory = new File(System.getProperty("user.dir") + "/" + SokoEngine.GAME_NAME + "-logfiles");
        directory.mkdirs();

        FileHandler fh = new FileHandler(directory + "/" + SokoEngine.GAME_NAME + ".log");
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
    }

    /**
     * Returns a {@link String} containing the current date and time, and the message.
     *
     * @param message - the message that should be appended to the {@link String}
     * @return - a {@link String} containing the current date and time, and the message
     */
    private String createFormattedMessage(String message) {
        return dateFormat.format(calendar.getTime()) + " -- " + message;
    }

    /**
     * {@inheritDoc}
     * @param message
     */
    public void info(String message) {
        logger.info(createFormattedMessage(message));
    }

    /**
     * {@inheritDoc}
     * @param message
     */
    public void warning(String message) {
        logger.warning(createFormattedMessage(message));
    }

    /**
     * {@inheritDoc}
     * @param message
     */
    public void severe(String message) {
        logger.severe(createFormattedMessage(message));
    }
}