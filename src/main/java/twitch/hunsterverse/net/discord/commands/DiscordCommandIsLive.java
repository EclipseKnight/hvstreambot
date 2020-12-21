package twitch.hunsterverse.net.discord.commands;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.twitch.chatbot.features.TwitchAPI;

public class DiscordCommandIsLive extends Command {

	public DiscordCommandIsLive() {
		this.name = "isLive";
		this.hidden = false;
		this.ownerCommand = true;
	}
	
	@Override
	protected void execute(CommandEvent event) {
		String[] args = event.getArgs().split("\\s+");
		
		event.reply(Arrays.toString(TwitchAPI.isLive(Arrays.asList(args))));
	}

}
