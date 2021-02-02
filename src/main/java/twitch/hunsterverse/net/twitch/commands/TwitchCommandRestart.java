package twitch.hunsterverse.net.twitch.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.Launcher;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.twitch.TwitchBot;
import twitch.hunsterverse.net.twitch.TwitchUtils;
import twitch.hunsterverse.net.twitch.features.TwitchAPI;

public class TwitchCommandRestart {

	public static void execute(ChannelMessageEvent event) {
		TwitchAPI.sendMessage(event, "Restarting twitch bot...");
		TwitchBot.twitchClient.close();
		Launcher.twitchBot = new TwitchBot();
	}
	
	public static void execute(CommandEvent event) {
		DiscordUtils.sendMessage(event, "```yaml\nRestarting twitch bot...\n```", false);
		TwitchBot.twitchClient.close();
		Launcher.twitchBot = new TwitchBot();
		DiscordUtils.setBotStatus(TwitchUtils.getLiveChannels().size() + " streamer(s)");
		
	}
}
