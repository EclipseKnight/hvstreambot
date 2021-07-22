package twitch.hunsterverse.net.discord.commands.owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

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
		//Grab the channelid because event will not exist after restart.
		String channelId = event.getChannel().getId();
		
		DiscordUtils.sendMessage(event, 
				DiscordUtils.createShortEmbed("Restarting Discord Bot...", null, DiscordBot.COLOR_SUCCESS, true), 
				false);
		
		long start = System.currentTimeMillis();
		
		//shutdown jda and construct new bot.
		DiscordBot.restart();
		
		long result = System.currentTimeMillis() - start;
		
		DiscordUtils.sendMessage(channelId, 
				DiscordUtils.createShortEmbed("Restart Finished!", "Time taken: " + result + "ms", 
						DiscordBot.COLOR_SUCCESS, 
						true)); 
				
	}

}
