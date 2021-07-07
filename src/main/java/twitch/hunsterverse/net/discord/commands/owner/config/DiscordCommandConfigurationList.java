package twitch.hunsterverse.net.discord.commands.owner.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.discord.commands.CommandUtils;

public class DiscordCommandConfigurationList extends Command {

	final String feature = "discord_command_config";
	public DiscordCommandConfigurationList() {
		this.name = "list";
		this.aliases = new String[] {"ls"};
	}

	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		event.reply("Soon TM");
//		event.reply(DiscordBot.configuration.toString());
	}
}
