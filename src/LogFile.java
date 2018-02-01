import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This is used to log
 */
public class LogFile {
    private Logger logger;

    /**
     * Configure the logger with handler and formatter.
     *
     * @param file_path where log message will be written to.
     * @throws IOException is thrown in case of failure of reading file.
     */
    public LogFile(String file_path) throws IOException {
        logger = Logger.getLogger("LogFile");
        FileHandler file_handler = new FileHandler(file_path);
        file_handler.setFormatter(new SimpleFormatter());
        logger.addHandler(file_handler);
    }

    /**
     * Log a information message into log file.
     *
     * @param msg information message.
     */
    public void logInfo(String msg) {
        logger.info(msg);
    }
}
