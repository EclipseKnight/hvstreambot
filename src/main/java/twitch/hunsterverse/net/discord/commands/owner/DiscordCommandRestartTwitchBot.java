package twitch.hunsterverse.net.discord.commands.owner;

import java.time.Instant;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;
import twitch.hunsterverse.net.twitch.commands.TwitchCommandRestart;

public class DiscordCommandRestartTwitchBot extends Command {

	final String feature = "discord_command_restart";
	public DiscordCommandRestartTwitchBot() {
		this.name = "twitch";	
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.appendDescription("Restarting Twitch Bot...");
		eb.setColor(DiscordBot.COLOR_SUCCESS);
		eb.setTimestamp(Instant.now());
		DiscordUtils.sendMessage(event, eb.build(), false);
		
		long start = System.currentTimeMillis();
		
		TwitchCommandRestart.execute(event);
		
		long result = System.currentTimeMillis() - start;
		
		eb.setDescription("Restart Finished!");
		eb.setFooter("Time taken: " + result + "ms");
		eb.setTimestamp(Instant.now());
		DiscordUtils.sendMessage(event, eb.build(), false);
		
	}

}
