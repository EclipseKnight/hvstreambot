package twitch.hunsterverse.net.discord.commands.owner.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;

public class DiscordCommandConfigurationReload extends Command {

	final String feature = "discord_command_config";
	public DiscordCommandConfigurationReload() {
		this.name = "reload";
	}

	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		DiscordBot.loadConfiguration();
		
		DiscordUtils.sendMessage(event, 
				DiscordUtils.createShortEmbed("Configuration reloaded!", 
						null,
						DiscordBot.COLOR_SUCCESS), false);
		
	}
}
