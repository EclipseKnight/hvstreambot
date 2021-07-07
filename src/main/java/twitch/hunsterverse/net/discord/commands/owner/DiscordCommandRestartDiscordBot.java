package twitch.hunsterverse.net.discord.commands.owner;

import java.time.Instant;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
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
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.appendDescription("Restarting Discord Bot...");
		eb.setColor(DiscordBot.COLOR_SUCCESS);
		eb.setTimestamp(Instant.now());
		DiscordUtils.sendMessage(event, eb.build(), false);
		
		long start = System.currentTimeMillis();
		
		//shutdown jda and construct new bot.
		DiscordBot.jda.shutdown();
		Launcher.discordBot = new DiscordBot();
		
		long result = System.currentTimeMillis() - start;
		
		eb.setDescription("Restart Finished!");
		eb.setFooter("Time taken: " + result + "ms");
		eb.setTimestamp(Instant.now());
		DiscordUtils.sendMessage(event, eb.build(), false);
	}

}
