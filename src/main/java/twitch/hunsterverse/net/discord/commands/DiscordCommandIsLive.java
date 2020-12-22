package twitch.hunsterverse.net.discord.commands;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.twitch.features.TwitchAPI;

public class DiscordCommandIsLive extends Command {

	public DiscordCommandIsLive() {
		this.name = DiscordBot.configuration.getFeatures().get("discord_command_is_live").getName();
		this.hidden = false;
		this.ownerCommand = true;
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!DiscordBot.configuration.getFeatures().get("discord_command_is_live").isEnabled()) {
			event.getChannel().sendMessage("The \""+ this.name + "\" is disabled.").queue( m -> {
				m.delete().queueAfter(5, TimeUnit.SECONDS);
			});
			
			return;
		}
		
		String[] args = event.getArgs().split("\\s+");
		
		event.reply(Arrays.toString(TwitchAPI.isLive(Arrays.asList(args))));
	}

}
