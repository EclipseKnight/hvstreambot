package twitch.hunsterverse.net.discord.commands.owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.commands.CommandUtils;

public class DiscordCommandBackup extends Command {

	final String feature = "discord_command_backup";
	public DiscordCommandBackup() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		JsonDB.backup();
	}

}
