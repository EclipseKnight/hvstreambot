package twitch.hunsterverse.net.discord.commands.owner.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;

public class DiscordCommandConfigurationReload extends Command {

	public DiscordCommandConfigurationReload() {
		this.name = "reload";
	}

	@Override
	protected void execute(CommandEvent event) {
		DiscordBot.loadConfiguration();
		DiscordUtils.sendMessage(event, """
				```yaml
				Configuration reloaded...
				```
				""", false);
	}
}
