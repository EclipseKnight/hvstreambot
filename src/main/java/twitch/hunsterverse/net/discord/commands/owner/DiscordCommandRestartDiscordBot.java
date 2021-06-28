package twitch.hunsterverse.net.discord.commands.owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.Launcher;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;

public class DiscordCommandRestartDiscordBot extends Command {

	final String feature = "discord_command_restart";
	public DiscordCommandRestartDiscordBot() {
		this.name = "discord";
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		DiscordUtils.sendMessage(event, "Restarting Discord Bot...", false);
		DiscordBot.jda.shutdown();
		Launcher.discordBot = new DiscordBot();
	}

}
