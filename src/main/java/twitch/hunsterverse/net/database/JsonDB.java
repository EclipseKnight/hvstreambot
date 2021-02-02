package twitch.hunsterverse.net.database;

import java.io.File;

import io.jsondb.JsonDBTemplate;
import io.jsondb.events.CollectionFileChangeListener;
import twitch.hunsterverse.net.Launcher;
import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;

public class JsonDB {

	public static String dbFilesLocation = Launcher.uwd  + File.separator + "hvstreambot" + File.separator + "database";
	private static String baseScanPackage = "twitch.hunsterverse.net.database";
	public static JsonDBTemplate database;
	
	public static void init() {
		if (new File(dbFilesLocation).mkdirs()) {
			Logger.log(Level.INFO, "Database directory created...");
		}
		
		database = new JsonDBTemplate(dbFilesLocation, baseScanPackage);
		
		if (!database.collectionExists(HVStreamer.class)) {
			database.createCollection(HVStreamer.class);
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
}
