package twitch.hunsterverse.net.updater;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import twitch.hunsterverse.net.Launcher;
import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;

public class Updater {

	public static String updaterPath = Launcher.uwd + File.separator + "hvstreambotupdater.jar";
	public static String os = System.getProperty("os.name").toLowerCase();
	
	/**
	 * runs the updater program and closes this one. 
	 */
	public static void update() {
		Thread updateHook = new Thread(() -> {
			Logger.log(Level.FATAL, "Initializing shutdown to update application...");
			ProcessBuilder processBuilder = new ProcessBuilder();
			
			if (os.contains("win")) {
				Logger.log(Level.INFO, "Starting updater...");
				processBuilder.command("cmd", "/c", "start", "cmd.exe", "/c", "java", "-jar", updaterPath, "--update");
				System.out.println(Arrays.toString(processBuilder.command().toArray()));
			}
			
			if (os.contains("linux")) {
				Logger.log(Level.INFO, "Starting updater...");
				Logger.log(Level.INFO, updaterPath);
				processBuilder.command("gnome-terminal", "--", "java", "-jar", updaterPath, "--update");
			}
			
			if (os.contains("mac")) {
				Logger.log(Level.INFO, "Starting updater...");
				processBuilder.command("/bin/bash", "-c", "java", "-jar", updaterPath, "--update");
			}
			
			try {
				processBuilder.start();
			} catch (IOException e) {
				e.printStackTrace();
				Logger.log(Level.FATAL, "Update process execution failed...");
			}
			
		});
		Runtime.getRuntime().addShutdownHook(updateHook);
		System.exit(0);
	}
}
