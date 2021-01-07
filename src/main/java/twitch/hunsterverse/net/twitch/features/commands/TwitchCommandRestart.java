package twitch.hunsterverse.net.twitch.features.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import twitch.hunsterverse.net.Launcher;
import twitch.hunsterverse.net.twitch.TwitchBot;
import twitch.hunsterverse.net.twitch.features.TwitchAPI;

public class TwitchCommandRestart {

	public static void execute(ChannelMessageEvent event) {
		TwitchAPI.sendMessage(event, "Restarting twitch bot...");
		TwitchBot.twitchClient.close();
		Launcher.twitchBot = new TwitchBot();
	}
}
