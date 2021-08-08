package twitch.hunsterverse.net.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import io.jsondb.JsonDBTemplate;
import io.jsondb.events.CollectionFileChangeListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.lingala.zip4j.ZipFile;
import twitch.hunsterverse.net.Launcher;
import twitch.hunsterverse.net.database.documents.ActiveEmbed;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.database.documents.HVStreamerConfig;
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
		
		if (!database.collectionExists(HVStreamerConfig.class)) {
			database.createCollection(HVStreamerConfig.class);
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
				init();
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
		
		File backupDir = new File(path);
		if (!backupDir.exists() && !backupDir.mkdirs()) {
			Logger.log(Level.FATAL, "Backup directory does not exist and could not be created. Backups are not being created.");
			return;
		}
		
		Logger.log(Level.WARN, "Backups are enabled. Interval: "+ interval + " hours");
		Logger.log(Level.WARN, "Backups are being stored at " + path);
		
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
			destPath = Launcher.uwd + File.separator + "hvstreambot" + File.separator + "backups";
		}
		
		// output to discord if channel available. 
		if (logChannel != null) {
			EmbedBuilder eb = new EmbedBuilder()
					.setTitle("Backing up database...")
					.setTimestamp(Instant.now())
					.setColor(DiscordBot.COLOR_FAILURE);
			DiscordUtils.sendMessage(logChannel, eb.build());
		}
		Logger.log(Level.WARN, "Backing up database...");
		
		long start = System.currentTimeMillis();
		
		//Start of backup sequence
		File srcDir = new File(srcPath);
		File destDir = new File(destPath);
		
		
		//Zip the database folder up and put it in the backups folder.
		ZipFile zipFile = null;
		String zipFilePath = destDir.getAbsolutePath() + File.separator + Instant.now().toString().replaceAll("[:]", "-") + ".zip";
		try {
			zipFile = new ZipFile(zipFilePath);
			zipFile.addFolder(srcDir);
			
		} catch (IOException e) {
			Logger.log(Level.ERROR, "IOException when trying to create backup zip file.");
			Logger.log(Level.ERROR, e.toString());
			
		} finally {
			try {
				zipFile.close();
			} catch (IOException e) {
				Logger.log(Level.ERROR, "IOException when trying to close zip stream.");
				Logger.log(Level.ERROR, e.toString());
			}
		}
		
		long result = System.currentTimeMillis() - start;
		
		File finalZip = new File(zipFilePath);
		
		// output to discord if channel available. 
		if (logChannel != null) {
			EmbedBuilder eb = new EmbedBuilder()
					.setTitle("Backup completed!")
					.appendDescription("Time taken: " + result + "ms")
					.setFooter("Backup size: " + humanReadableByteCount(finalZip.length(), false))
					.setTimestamp(Instant.now())
					.setColor(DiscordBot.COLOR_SUCCESS);
			DiscordUtils.sendMessage(logChannel, eb.build());
		}
		Logger.log(Level.SUCCESS, "Backup completed. Time taken (MS): " + result);
		
		deleteOldestBackup(destDir);
	}
	
	/**
	 * Deletes the oldest log passed the max stored value of logs. 
	 * @param dir file directory
	 */
	private static void deleteOldestBackup(File dir) {
 		File[] backupFiles = dir.listFiles();
 		long oldestDate = Long.MAX_VALUE;
 		File oldestFile = null;
 		
 		//delete the oldest log if there are more than backup_quantity.
 		if (backupFiles != null && backupFiles.length > Integer.valueOf(DiscordBot.configuration.getDatabase().get("backup_quantity"))) {
 			
 			for (File f: backupFiles) {
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
	
	
	
	public static String folderSize(File directory) {
		long size = -1;
		try {
			size = Files.walk(directory.toPath())
				      .filter(p -> p.toFile().isFile())
				      .mapToLong(p -> p.toFile().length())
				      .sum();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	    return humanReadableByteCount(size, false);
	}
	
	/*
	 * copied from stackoverflow.
	 */
	private strictfp static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    long absBytes = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
	    if (absBytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(absBytes) / Math.log(unit));
	    long th = (long) Math.ceil(Math.pow(unit, exp) * (unit - 0.05));
	    if (exp < 6 && absBytes >= th - ((th & 0xFFF) == 0xD00 ? 51 : 0)) exp++;
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
	    if (exp > 4) {
	        bytes /= unit;
	        exp -= 1;
	    }
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
}
