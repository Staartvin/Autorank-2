package me.armar.plugins.autorank.logger;

import me.armar.plugins.autorank.Autorank;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LoggerManager {

    public final static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Autorank autorank;
    private LogFile logFile;

    public LoggerManager(Autorank autorank) {
        this.autorank = autorank;
        this.loadLogFile();
    }

    /**
     * Log a message to the log file.
     *
     * @param message Message to log, cannot be null.
     */
    public void logMessage(String message) {
        if (message == null) return;

        LogFile currentLogFile = getCurrentLogFile();

        if (currentLogFile == null) {
            autorank.getLogger().severe("Autorank could not start its logger. Autorank will not log!");
            return;
        }

        if (!currentLogFile.isFileReady()) {
            autorank.getLogger().severe("Autorank create a log file but can't load it. Autorank will not log!");
            return;
        }

        currentLogFile.writeToFile(message);
    }

    /**
     * Get the currently active log file. If no file was active, a new active log file is created.
     *
     * @return Log file used for logging.
     */
    private LogFile getCurrentLogFile() {
        if (logFile == null) {
            this.generateNewLogFile();
        }

        return logFile;
    }

    /**
     * Generate a new log file to write to.
     */
    private void generateNewLogFile() {
        LocalDate logFileDate = LocalDate.now();

        logFile = new LogFile(autorank.getDataFolder().getAbsolutePath() + File.separator
                + "logging" + File.separator + "log-" + dateFormat.format(logFileDate) + ".txt");

        logFile.loadFile();
    }

    /**
     * Load a log file to start writing to.
     */
    public void loadLogFile() {
        this.generateNewLogFile();
    }


}
