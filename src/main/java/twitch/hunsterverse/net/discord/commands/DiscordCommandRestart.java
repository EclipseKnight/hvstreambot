package twitch.hunsterverse.net.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.Launcher;
import twitch.hunsterverse.net.discord.DiscordBot;

public class DiscordCommandRestart extends Command {

	public DiscordCommandRestart() {
		this.name = "restart";
		this.ownerCommand = true;
		this.hidden = true;
	}
	
	
	@Override
	protected void execute(CommandEvent event) {
		event.reply("Restarting Discord Bot...");
		DiscordBot.jda.shutdown();
		Launcher.discordBot = new DiscordBot();
	}

}
