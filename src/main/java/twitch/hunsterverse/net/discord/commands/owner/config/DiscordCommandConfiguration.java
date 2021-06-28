package twitch.hunsterverse.net.discord.commands.owner.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;

public class DiscordCommandConfiguration extends Command {

	final String feature = "discord_command_config";
	public DiscordCommandConfiguration() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.children = new Command[] {new DiscordCommandConfigurationList(), new DiscordCommandConfigurationReload()};
		this.ownerCommand = true;
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		DiscordUtils.sendTimedMessage(event, """
				```yaml
				Invalid Arguments: config [list, ls] or [reload].
				```
				""", 10000, false);
		
	}

}
