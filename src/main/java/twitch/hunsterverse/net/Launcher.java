package twitch.hunsterverse.net;

import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.twitch.chatbot.TwitchBot;

public class Launcher {
	
	public static TwitchBot twitchBot;
	public static DiscordBot discordBot;
	
	public static void main(String[] args) {
		twitchBot = new TwitchBot();
		twitchBot.registerFeatures();
		twitchBot.start();
		
		discordBot = new DiscordBot();
	}
}
