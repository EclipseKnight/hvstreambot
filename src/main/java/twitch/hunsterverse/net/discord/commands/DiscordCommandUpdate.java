package twitch.hunsterverse.net.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;
import twitch.hunsterverse.net.updater.Updater;

public class DiscordCommandUpdate extends Command {

	public DiscordCommandUpdate() {
		this.name = "update";
		this.ownerCommand = true;
		this.hidden = true;
	}
	
	
	@Override
	protected void execute(CommandEvent event) {
		Logger.log(Level.FATAL, "Updating Discord Bot...");
		event.reply("Updating Discord Bot...");
		Updater.update();
	}

}
