package twitch.hunsterverse.net.discord.commands.owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.discord.commands.CommandUtils;
import twitch.hunsterverse.net.twitch.commands.TwitchCommandRestart;

public class DiscordCommandRestartTwitchBot extends Command {

	final String feature = "discord_command_restart";
	public DiscordCommandRestartTwitchBot() {
		this.name = "twitch";	
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		TwitchCommandRestart.execute(event);
	}

}
