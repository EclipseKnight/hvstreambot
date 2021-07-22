package twitch.hunsterverse.net.twitch.command.commands;

import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.Launcher;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.twitch.TwitchBot;
import twitch.hunsterverse.net.twitch.TwitchUtils;
import twitch.hunsterverse.net.twitch.command.TwitchCommand;
import twitch.hunsterverse.net.twitch.command.TwitchCommandEvent;
import twitch.hunsterverse.net.twitch.features.TwitchAPI;

public class TwitchCommandRestart extends TwitchCommand {
	
	
	public TwitchCommandRestart() {
		this.feature = "twitch_command_restart";
		this.name = TwitchBot.configuration.getFeatures().get(feature).getName();
		this.aliases = TwitchBot.configuration.getFeatures().get(feature).getAliases();
	}
	
	@Override
	protected void execute(TwitchCommandEvent event) {
		if (TwitchUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		TwitchAPI.sendMessage(event, "Restarting twitch bot...");
		TwitchBot.twitchClient.close();
		Launcher.twitchBot = new TwitchBot();
	}
	
	// for discord execution.
	public static void execute(CommandEvent event) {
		TwitchBot.twitchClient.close();
		Launcher.twitchBot = new TwitchBot();
		DiscordUtils.setBotStatus(TwitchUtils.getLiveFilteredChannels().size() + " streamer(s)");
		
	}
}
