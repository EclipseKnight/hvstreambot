package twitch.hunsterverse.net.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import twitch.hunsterverse.net.Launcher;

/**
 * Not going to bother user an existing logger since this project is riddled
 * with them. So here is a simple class just to do some console logging for me.
 * 
 * @author EclipseKnight
 *
 */
public class Logger {
	
	/*
	 * Path to the directory where logs are stored. 
	 */
	public static String PATH = Launcher.uwd  + File.separator + "hvstreambot" + File.separator + "logs";
	
	public static final String RESET = "\033[0m";  // Text Reset

	public static final String PURPLE = "\033[0;35m";  // PURPLE
	public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE
	public static final String RED_BRIGHT = "\033[0;91m";    // RED
    public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
    
    private static File log = null;
    private static BufferedWriter writer;
    
    
    /**
     * Enum info:
     * 
     * <br>
     * <b>DEBUG</b> - information events that are most useful for debugging.
     * <br>
     * <b>INFO</b> - informational messages that highlight progress of the application.
     * <br>
     * <b>WARN</b> - potentially harmful situations.
     * <br>
     * <b>ERROR</b> - error events that might still allow the application to continue running.
     * <br>
     * <b>FATAL</b> - severe error events that will presumably lead the application to terminate.
     * <br>
     * <b>SUCCCESS</b> - events that indicate successfully completing tasks.
     * <br>
     * <b>CHAT</b> - a twitch chat message.
     * @author EclipseKnight
     *
     */
	public enum Level {
		DEBUG, INFO, WARN, ERROR, FATAL, SUCCESS, CHAT
	}

	public static void log(Level level, String message) {
		Date date = Calendar.getInstance().getTime();
	
		if (!generateLog(PATH)) {
			System.out.println(date + RED_BRIGHT + " [" + Level.FATAL.toString() + "] unable to create directory or file needed to store logs. Logs will NOT be saved!" + RESET);
		}
		
		String m = null;
		switch (level) {
			case CHAT -> {
				m = date + PURPLE + " [" + level.toString() + "] " + message + RESET;
				System.out.println(m);
				m = date +  " [" + level.toString() + "] " + message;
				writeToLog(m);
			}
			case SUCCESS -> {
				m = date + GREEN_BRIGHT + " [" + level.toString() + "] " + message + RESET;
				System.out.println(m);
				m = date + " [" + level.toString() + "] " + message;
				writeToLog(m);
			}
			case DEBUG, INFO -> {
				m = date + WHITE_BRIGHT + " [" + level.toString() + "] " + message + RESET;
				System.out.println(m);
				m = date + " [" + level.toString() + "] " + message;
				writeToLog(m);
			}
			case WARN, ERROR -> {
				m = date + YELLOW_BRIGHT + " [" + level.toString() + "] " + message + RESET;
				System.out.println(m);
				m = date + " [" + level.toString() + "] " + message;
				writeToLog(m);
			}
			case FATAL -> {
				m = date + RED_BRIGHT + " [" + level.toString() + "] " + message + RESET;
				System.out.println(m);
				m = date + " [" + level.toString() + "] " + message;
				writeToLog(m);
			}
			default -> {
				log(Level.ERROR, "Unexpected Level value: " + level);
			}
		}
		
	}
	
	private static boolean generateLog(String path) {
		if (log != null) {
			return true;
		}
		
		File dir = new File(path);
		if (!dir.mkdirs() && !dir.exists()) {
			return false;
		}
		
		log = new File(path, Instant.now().toString().replaceAll("[:]", "-") + ".txt");
		log(Level.INFO, "Logs being stored at " + log.getPath());
		
		try {
			log.createNewFile();
			writer = new BufferedWriter(new FileWriter(log));
			
		} catch (IOException e) {
			log(Level.ERROR, "IOException occurred when trying to create log file.");
			log(Level.ERROR, e.toString());
			
			return false;
		}
		
		deleteOldestLog(dir);
		addShutdownHook();
		return true;
	}
	

	/**
	 * Deletes the oldest log passed the max stored value of logs. 
	 * @param dir file directory
	 */
	private static void deleteOldestLog(File dir) {
 		File[] logFiles = dir.listFiles();
 		long oldestDate = Long.MAX_VALUE;
 		File oldestFile = null;
 		
 		//delete the oldest log if there are more than 15.
 		if (logFiles != null && logFiles.length > 5) {
 			
 			for (File f: logFiles) {
 				if (f.lastModified() < oldestDate) {
 					oldestDate = f.lastModified();
 					oldestFile = f;
 				}
 			}
 		}
 		
 		if (oldestFile != null) {
 			oldestFile.delete();
 		}
	}
	
	public static void writeToLog(String string) {
		if (writer == null) {
			return;
		}
		
		try {
			writer.append(string + "\n");
			writer.flush();
			
		} catch (IOException e) {
			log(Level.ERROR, "IOException occurred when trying to write to the log file.");
			log(Level.ERROR, log.toString());
		}
	}
	
	private static void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread( () -> {
			try {
				log(Level.WARN, "Logger shutdown hook started...");
				writer.close();
			} catch (IOException e) {
				log(Level.ERROR, "Logger shutdown hook failed to close writer.");
				log(Level.ERROR, e.toString());
			}
			
		}));
	}
}
