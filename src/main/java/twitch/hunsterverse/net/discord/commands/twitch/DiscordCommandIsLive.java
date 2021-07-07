package twitch.hunsterverse.net.discord.commands.twitch;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;
import twitch.hunsterverse.net.twitch.features.TwitchAPI;

public class DiscordCommandIsLive extends Command {

	final String feature = "discord_command_is_live";
	public DiscordCommandIsLive() {
		this.name = DiscordBot.configuration.getFeatures().get("discord_command_is_live").getName();
		this.hidden = false;
		this.ownerCommand = true;
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		String[] args = event.getArgs().split("\\s+");
		
		event.reply(DiscordUtils.createShortEmbed(Arrays.toString(TwitchAPI.isLive(Arrays.asList(args))), null, DiscordBot.COLOR_STREAMER));
	}

}
