package twitch.hunsterverse.net.discord.commands.owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;

public class DiscordCommandRestart extends Command {

	
	final String feature = "discord_command_restart";
	public DiscordCommandRestart() {
		this.name = "restart";
		this.children = new Command[] {new DiscordCommandRestartDiscordBot(), new DiscordCommandRestartTwitchBot()};
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		DiscordUtils.sendTimedMessage(event,
				DiscordUtils.createShortEmbed("Invalid Arguments", 
						DiscordBot.PREFIX + "restart [twitch, discord]",
						DiscordBot.COLOR_FAILURE), 10000, false);
	}

}
