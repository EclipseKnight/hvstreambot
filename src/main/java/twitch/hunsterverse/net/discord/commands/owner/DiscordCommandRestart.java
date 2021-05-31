package twitch.hunsterverse.net.discord.commands.owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;

public class DiscordCommandRestart extends Command {

	
	String feature = "discord_command_restart";
	public DiscordCommandRestart() {
		this.name = "restart";
		this.children = new Command[] {new DiscordCommandRestartDiscordBot(), new DiscordCommandRestartTwitchBot()};
	}
	
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		DiscordUtils.sendTimedMessaged(event, """
				```yaml
				Invalid Arguments: restart [twitch, discord]
				```
				""", 10000, false);
	}

}
