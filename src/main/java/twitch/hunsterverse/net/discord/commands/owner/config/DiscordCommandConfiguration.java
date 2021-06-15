package twitch.hunsterverse.net.discord.commands.owner.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.discord.DiscordUtils;

public class DiscordCommandConfiguration extends Command {

	public DiscordCommandConfiguration() {
		this.name = "config";
		this.children = new Command[] {new DiscordCommandConfigurationList(), new DiscordCommandConfigurationReload()};
		this.ownerCommand = true;
	}
	
	@Override
	protected void execute(CommandEvent event) {
		DiscordUtils.sendTimedMessage(event, """
				```yaml
				Invalid Arguments: config [list, ls] or [reload].
				```
				""", 10000, false);
		
	}

}
