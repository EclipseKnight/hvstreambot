package twitch.hunsterverse.net.discord.commands;

import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.discord.DiscordBot;

public class DiscordCommandConfiguration extends Command {

	public DiscordCommandConfiguration() {
		this.name = "config";
		this.ownerCommand = true;
		this.hidden = true;
	}
	
	@Override
	protected void execute(CommandEvent event) {
		String[] args = event.getArgs().split("\\s+");
		
		if (args[0].isEmpty()) {
			event.getChannel().sendMessage("Invalid Arguments: Use [list, ls] or [reload].").queue( m -> {
				m.delete().queueAfter(5, TimeUnit.SECONDS);
			});
			return;
		}
		
		if ("list".equals(args[0]) || "ls".equals(args[0])) {
			event.reply(DiscordBot.configuration.toString());
			return;
		}
		
		if ("reload".equals(args[0])) {
			DiscordBot.loadConfiguration();
			event.reply("Configuration reloaded...\n" + DiscordBot.configuration.toString());
			return;
		}
		
	}

}
