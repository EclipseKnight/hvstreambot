package twitch.hunsterverse.net.discord.commands.owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;

public class DiscordCommandEmbedUpdate extends Command {

	final String feature = "discord_command_embed_update";
	public DiscordCommandEmbedUpdate() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) 
			return;
		
		event.reply(String.format("""
				```yaml
				Updating embeds...
				```
				"""));
		long start = System.currentTimeMillis();
		DiscordUtils.updateLiveEmbeds(true);
		long result = System.currentTimeMillis() - start;
		event.reply(String.format("""
				```yaml
				Finished updating... Time taken (MS): %s
				```
				""", result));
	}

}
