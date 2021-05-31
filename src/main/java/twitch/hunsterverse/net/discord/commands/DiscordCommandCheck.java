package twitch.hunsterverse.net.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.twitch.features.TwitchAPI;

public class DiscordCommandCheck extends Command {

	final String feature = "discord_command_check";
	
	public DiscordCommandCheck() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases = DiscordBot.configuration.getFeatures().get(feature).getAliases();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature))
			return;
		
		String[] args = CommandUtils.splitArgs(event.getArgs());
		
		if (args.length < 1) {
			DiscordUtils.sendTimedMessaged(event, """
					```yaml
					Invalid Arguments: check [<@discorduser> OR <twitchchannel>];
					```
					""", 10000, false);
			return;
		}
		
		String discordId = CommandUtils.getIdFromMention(args[0]);
		String channel = args[0].trim();
		
		// Check for valid snowflake
		if (CommandUtils.isValidSnowflake(discordId)) {
			
			// query for snowflake in db and check for active link.
			if (CommandUtils.getUserWithDiscordId(discordId) != null) {
				
				HVStreamer s = CommandUtils.getUserWithDiscordId(discordId);
				
				DiscordUtils.sendMessage(event, String.format("""
						```yaml
						Check Results... | User: %s | TwitchChannel: %s | affiliate: %s | linked: %s
						```
						""", "<@"+discordId+">", s.getTwitchChannel(), s.isAffiliate(), s.isLinked()), false);
				return;
			}
			
			DiscordUtils.sendTimedMessaged(event, """
					```yaml
					Invalid Arguments: Channel or user does not exist.
					```
					""", 10000, false);
			
			return;
		} 
		
		// Check if channel exists
		if (TwitchAPI.isChannel(channel)) {
			// Query for user linked to the channel.
			if (CommandUtils.getUserWithTwitchChannel(channel) != null
					&& CommandUtils.getUserWithTwitchChannel(channel).isLinked()) {
				
				HVStreamer s = CommandUtils.getUserWithTwitchChannel(channel);

				DiscordUtils.sendMessage(event, String.format("""
						```yaml
						Check Results... | User: %s | TwitchChannel: %s | affiliate: %s | linked: %s
						```
						""", "<@"+s.getDiscordId()+">", s.getTwitchChannel(), s.isAffiliate(), s.isLinked()), false);
				return;
			}
		}
		
		
		// In case neither conditions are met. 
		DiscordUtils.sendTimedMessaged(event, """
				```yaml
				Invalid Arguments: Channel or user does not exist.
				```
				""", 10000, false);
		return;
	}

}
