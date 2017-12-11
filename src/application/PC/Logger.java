package application;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import lejos.util.Stopwatch;

/**
 * Logger Class
 * 
 * Implements remote logger based on the RConsole
 */
public class Logger {

	private static final int LOGGER_DEFAULT_TIMEOUT = 5000;
	private static final char LOG_FILE_DELIMITER = ',';
	private static final String LOG_FILE_NAME = "c:\\log.csv";
	private static final char CONSOLE_DELIMITER = '\t';

	private static PrintStream console;
	private static File logFile;
	private static DataOutputStream logFileStream;
	private static Stopwatch timeSincePower;
	private static LoggerLevel level;

	/**
	 * Initialization
	 * 
	 * @param level logger severity level
	 */
	public static void init(LoggerLevel level) {
		timeSincePower = new Stopwatch();
		timeSincePower.reset();
		Logger.level = level;
		openLogFile();
		connectConsole(0);
	}

	/**
	 * Open the log file
	 */
	private static void openLogFile() {
		logFile = new File(LOG_FILE_NAME);
		try {
			logFile.createNewFile();
			logFileStream = new DataOutputStream(new FileOutputStream(logFile));
		} catch (IOException e) {}
	}

	/**
	 * Open connection to the RConsole
	 * 
	 * @param timeout timeout for console connection
	 */
	public static void connectConsole(int timeout) {
		console = System.out;
	}

	/**
	 * Open connection to the RConsole
	 */
	public static void connectConsole() {
		connectConsole(LOGGER_DEFAULT_TIMEOUT);
	}

	/**
	 * Write to logger
	 * 
	 * @param level logger severity level
	 * @param group logger group
	 * @param text message to print
	 */
	public static void log(LoggerLevel level, LoggerGroup group, String text) {
		if (level.getValue() >= Logger.level.getValue()) {
			writeToConsole(level, group, text);
		}
		writeToLogFile(level, group, text);		
	}

	/**
	 * Write to log file
	 *
	 * @param level logger level
	 * @param group logger group
	 * @param text message to print
	 */
	private static void writeToLogFile(LoggerLevel level, LoggerGroup group, String text) {
		try {
			logFileStream.writeBytes(String.valueOf(timeSincePower.elapsed()) + LOG_FILE_DELIMITER + level
					+ LOG_FILE_DELIMITER + group + LOG_FILE_DELIMITER + text.replace(',', ' ') + "\r\n");
		} catch (IOException e) {}
	}

	/**
	 * Write to console
	 *
	 * @param level logger level
	 * @param group logger group
	 * @param text message to print
	 */
	private static void writeToConsole(LoggerLevel level, LoggerGroup group, String text) {		
		console.println(String.valueOf(timeSincePower.elapsed()) + CONSOLE_DELIMITER + level + CONSOLE_DELIMITER + group + CONSOLE_DELIMITER + text);
	}
	
	/**
	 * Close logger
	 */
	public static void close() {
		try {
			logFileStream.close();
		} catch (IOException e) {}
	}
}