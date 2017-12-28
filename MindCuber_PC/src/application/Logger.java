package application;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import lejos.util.Stopwatch;

/**
 * This class implements the project's logger 
 */
public class Logger {

	private static final char LOG_FILE_DELIMITER = ',';
	private static final String LOG_FILE_NAME = "log.csv";
	private static final char CONSOLE_DELIMITER = '\t';

	private static File logFile;
	private static DataOutputStream logFileStream;
	private static Stopwatch timeSincePower;
	private static LoggerLevel level;

	/**
	 * Logger initialization
	 * 
	 * @param level The logger's severity level
	 */
	public static void init(LoggerLevel level) {
		timeSincePower = new Stopwatch();
		timeSincePower.reset();
		Logger.level = level;
		openLogFile();
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
	 * Write to logger
	 * 
	 * @param level The logger's severity level
	 * @param group The logger's group
	 * @param text Text message to print
	 * @see LoggerLevel
	 * @see LoggerGroup
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
	 * @param level The logger's severity level
	 * @param group The logger's group
	 * @param text Text message to print
	 * @see LoggerLevel
	 * @see LoggerGroup
	 */
	private static void writeToLogFile(LoggerLevel level, LoggerGroup group, String text) {
		try {
			if (logFileStream != null) {
				logFileStream.writeBytes(String.valueOf(timeSincePower.elapsed()) + LOG_FILE_DELIMITER + level
						+ LOG_FILE_DELIMITER + group + LOG_FILE_DELIMITER + text.replace(',', ' ') + "\r\n");
			}
		} catch (IOException e) {}
	}

	/**
	 * Write to console
	 *
	 * @param level The logger's severity level
	 * @param group The logger's group
	 * @param text Text message to print
	 * @see LoggerLevel
	 * @see LoggerGroup
	 */
	private static void writeToConsole(LoggerLevel level, LoggerGroup group, String text) {		
		System.out.println(String.valueOf(timeSincePower.elapsed()) + CONSOLE_DELIMITER + level + CONSOLE_DELIMITER + group + CONSOLE_DELIMITER + text);
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