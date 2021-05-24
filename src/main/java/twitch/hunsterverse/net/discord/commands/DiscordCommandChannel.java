package twitch.hunsterverse.net.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.discord.DiscordBot;

public class DiscordCommandChannel extends Command {

	String feature = "discord_command_channel";
	
	public DiscordCommandChannel() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return; 
		}
		
		String[] args = CommandUtils.splitArgs(event.getArgs());
		String discordId = CommandUtils.getIdFromMention(args[0]);
		
		HVStreamer streamer = null;
		
		if (CommandUtils.isValidSnowflake(discordId)) {
			if (CommandUtils.getUserWithDiscordId(discordId) != null) {
				streamer = CommandUtils.getUserWithDiscordId(discordId);
			}
		}
		
		if (streamer == null && CommandUtils.getUserWithDiscordName(args[0]) != null) {
			streamer = CommandUtils.getUserWithDiscordName(args[0]);
		}
		
		
	}
}
