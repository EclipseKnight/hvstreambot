package twitch.hunsterverse.net;

import org.fusesource.jansi.AnsiConsole;

import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.twitch.TwitchBot;

public class Launcher {
	
	public static TwitchBot twitchBot;
	public static DiscordBot discordBot;
	public static String configPath = System.getProperty("user.dir");
	
	public static void main(String[] args) {
		// allows ANSI escape sequences to format console output. For loggers. aka PRETTY COLORS
		AnsiConsole.systemInstall();
				
		twitchBot = new TwitchBot();
		twitchBot.registerFeatures();
		twitchBot.start();
		
		discordBot = new DiscordBot();
	}
}
