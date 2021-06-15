package twitch.hunsterverse.net.database;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import io.jsondb.JsonDBTemplate;
import io.jsondb.events.CollectionFileChangeListener;
import twitch.hunsterverse.net.Launcher;
import twitch.hunsterverse.net.database.documents.ActiveEmbed;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.database.documents.HVUser;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;

public class JsonDB {

	public static String dbFilesLocation = Launcher.uwd  + File.separator + "hvstreambot" + File.separator + "database";
	private static String baseScanPackage = "twitch.hunsterverse.net.database.documents";
	public static JsonDBTemplate database;
	
	private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public static TimeBasedGenerator gen = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
	
	public static void init() {
		if (new File(dbFilesLocation).mkdirs()) {
			Logger.log(Level.INFO, "Database directory created...");
		}
		
		database = new JsonDBTemplate(dbFilesLocation, baseScanPackage);
		
		if (!database.collectionExists(HVStreamer.class)) {
			database.createCollection(HVStreamer.class);
		}
		
		if (!database.collectionExists(HVUser.class)) {
			database.createCollection(HVUser.class);
		}
		
		if (!database.collectionExists(ActiveEmbed.class)) {
			database.createCollection(ActiveEmbed.class);
		}
		
		database.addCollectionFileChangeListener(new CollectionFileChangeListener() {
			
			@Override
			public void collectionFileModified(String collectionName) {
				database.reloadCollection(collectionName);
				Logger.log(Level.WARN, "Collection File Modified: " + collectionName +"\nReloaded Collection.");
			}
			
			@Override
			public void collectionFileDeleted(String collectionName) {
				database.reLoadDB();
				Logger.log(Level.WARN, "Collection File Deleted: " + collectionName +"\nReloaded Database.");
			}
			
			@Override
			public void collectionFileAdded(String collectionName) {
				database.reLoadDB();
				Logger.log(Level.WARN, "Collection File Added: " + collectionName +"\nReloaded Database.");
			}
		});
		
		
		
	}
	
	/**
	 * Initialize scheduled backups after creation of DiscordBot. DiscordBot configuration is needed.
	 */
	public static void initScheduledBackups() {

		// Scheduled backups.
		if (!Boolean.valueOf(DiscordBot.configuration.getDatabase().get("backup_enabled"))) {
			Logger.log(Level.WARN, "Backups are disabled.");
			return;
		}
		
		final int interval = Integer.valueOf(DiscordBot.configuration.getDatabase().get("backup_interval"));
		
		String path = DiscordBot.configuration.getDatabase().get("backup_path");
		
		if (path == null) {
			path = Launcher.uwd  + File.separator + "hvstreambot" + File.separator + "backups";
		}
		
		Logger.log(Level.WARN, "Backups are enabled. Interval: "+ interval + " hours\nStored at " + path);
		
		JsonDB.scheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				backup();
			}
			
		}, interval, interval, TimeUnit.HOURS);
	}
	
	public static void backup() {
		String destPath = DiscordBot.configuration.getDatabase().get("backup_path");
		String srcPath = dbFilesLocation;
		
		String logChannel = DiscordBot.configuration.getDatabase().get("backup_log_channel");
		
		if (destPath == null) {
			destPath = Launcher.uwd  + File.separator + "hvstreambot" + File.separator + "backups";
		}
		
		// output to discord if channel available. 
		if (logChannel != null) {
			DiscordUtils.sendMessage(logChannel, """
					```yaml
					Backing up database...
					```
					""");
		}
		Logger.log(Level.WARN, "Backing up database...");
		
		long start = System.currentTimeMillis();
		
		//Start of backup sequence
		File srcDir = new File(srcPath);
		File destDir = new File(destPath);
		
		try {
			FileUtils.copyDirectory(srcDir, destDir);
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		long result = System.currentTimeMillis() - start;
		
		// output to discord if channel available. 
		if (logChannel != null) {
			DiscordUtils.sendMessage(logChannel, String.format("""
					```yaml
					Backup completed. Time take (MS): %s 
					```
					""", result));
		}
		Logger.log(Level.SUCCESS, "Backup completed. Time taken (MS): " + result);
	}
}
