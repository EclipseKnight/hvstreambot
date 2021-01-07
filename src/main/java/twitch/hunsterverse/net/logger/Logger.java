package twitch.hunsterverse.net.logger;

import java.util.Calendar;
import java.util.Date;

/**
 * Not going to bother user an existing logger since this project is riddled
 * with them. So here is a simple class just to do some console logging for me.
 * 
 * @author EclipseKnight
 *
 */
public class Logger {
	public static final String RESET = "\033[0m";  // Text Reset

	public static final String PURPLE = "\033[0;35m";  // PURPLE
	
	public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE
	public static final String RED_BRIGHT = "\033[0;91m";    // RED
    public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
	

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
		
		switch (level) {
			case CHAT -> System.out.println(date + PURPLE + " [" + level.toString() + "] " + message + RESET);
			case SUCCESS -> System.out.println(date + GREEN_BRIGHT + " [" + level.toString() + "] " + message + RESET);
			case DEBUG, INFO -> System.out.println(date + WHITE_BRIGHT + " [" + level.toString() + "] " + message + RESET);
			case WARN, ERROR -> System.out.println(date + YELLOW_BRIGHT + " [" + level.toString() + "] " + message + RESET);
			case FATAL -> System.out.println(date + RED_BRIGHT + " [" + level.toString() + "] " + message + RESET);
			default -> throw new IllegalArgumentException("Unexpected value: " + level);
		}
	}
}
