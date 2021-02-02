package twitch.hunsterverse.net.discord.commands.owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.Launcher;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;

public class DiscordCommandRestartDiscordBot extends Command {

	public DiscordCommandRestartDiscordBot() {
		this.name = "discord";
	}
	
	@Override
	protected void execute(CommandEvent event) {
		DiscordUtils.sendMessage(event, "Restarting Discord Bot...", false);
		DiscordBot.jda.shutdown();
		Launcher.discordBot = new DiscordBot();
	}

}
