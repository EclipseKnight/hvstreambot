package twitch.hunsterverse.net.discord.commands.owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.twitch.commands.TwitchCommandRestart;

public class DiscordCommandRestartTwitchBot extends Command {

	public DiscordCommandRestartTwitchBot() {
		this.name = "twitch";	
	}
	
	@Override
	protected void execute(CommandEvent event) {
		TwitchCommandRestart.execute(event);
	}

}
