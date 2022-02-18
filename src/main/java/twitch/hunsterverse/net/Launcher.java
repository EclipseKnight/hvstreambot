package twitch.hunsterverse.net;

import org.fusesource.jansi.AnsiConsole;

import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;
import twitch.hunsterverse.net.twitch.TwitchBot;

public class Launcher {
	
	public static TwitchBot twitchBot;
	public static DiscordBot discordBot;
	public static String uwd = System.getProperty("user.dir");
	
	public static void main(String[] args) {
		// allows ANSI escape sequences to format console output. For loggers. aka PRETTY COLORS
		AnsiConsole.systemInstall();
		
		// initialize the database
		Logger.log(Level.INFO, "Database initialized...");
		JsonDB.init();
		
		Logger.log(Level.INFO, "Twitch Bot initialized...");
		twitchBot = new TwitchBot();
		twitchBot.registerFeatures();
		twitchBot.start();
		
		Logger.log(Level.INFO, "Discord Bot initialized...");
		discordBot = new DiscordBot();
		
		
		// initialize scheduled backups after discord bot creation. 
		// This utilizes the discord bot configuration file instead of a separate config.
		JsonDB.initScheduledBackups();
		
	}
}
