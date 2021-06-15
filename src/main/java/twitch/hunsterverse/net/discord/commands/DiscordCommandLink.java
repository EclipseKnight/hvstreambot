package twitch.hunsterverse.net.discord.commands;

import java.util.HashMap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.twitch.commands.TwitchCommandRestart;
import twitch.hunsterverse.net.twitch.features.TwitchAPI;

public class DiscordCommandLink extends Command {

	final String feature = "discord_command_link";
	public DiscordCommandLink() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases = DiscordBot.configuration.getFeatures().get(feature).getAliases();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) 
			return;
		
		// get args.
		String[] args = CommandUtils.splitArgs(event.getArgs());
		
		if (args.length < 3) {
			DiscordUtils.sendTimedMessage(event, """
					```yaml
					Invalid Arguments: link <@discorduser> <twitchchannel> [<affiliate> true, false]
					```
					""", 10000, false);
			return;
		}
			
		
		String discordId = CommandUtils.getIdFromMention(args[0]);
		if (!CommandUtils.isValidSnowflake(discordId)) {
			DiscordUtils.sendTimedMessage(event, """
					```yaml
					Invalid Snowflake.
					```
					""", 10000, false);
			return;
		}
		
		String channel = args[1].trim();
		if (!TwitchAPI.isChannel(channel)) {
			DiscordUtils.sendTimedMessage(event, """
					```yaml
					Channel does not exist.
					```
					""", 10000, false);
			return;
		}
		
		boolean affiliate = Boolean.valueOf(args[2]);

		// If streamer is already linked.
		if (CommandUtils.getStreamerWithDiscordId(discordId) != null && CommandUtils.getStreamerWithDiscordId(discordId).isLinked()) {
			HVStreamer s = CommandUtils.getStreamerWithDiscordId(discordId);
			s.setLinked(true);
			s.setTwitchChannel(channel);
			s.setAffiliate(affiliate);
			
			JsonDB.database.upsert(s);
			
			DiscordUtils.giveRole(event, discordId, DiscordBot.configuration.getStreamRoleId());
			DiscordUtils.sendMessage(event, String.format("""
					```yaml
					Successfully Relinked! | User: %s | TwitchChannel: %s | affiliate: %s
					```
					""", "<@"+discordId+">", channel, affiliate), false);
			
			// Restart twitch bot to register new channel listener
			TwitchCommandRestart.execute(event);
			return;
		}
		
		
		//New streamer
		HVStreamer s = new HVStreamer();
		s.setDiscordId(discordId);
		s.setDiscordName(DiscordBot.jda.retrieveUserById(discordId).complete().getAsTag());
		s.setTwitchChannel(channel);
		s.setLinked(true);
		s.setAffiliate(affiliate);
		s.setPingable(false);
		s.setStreaming(TwitchAPI.isLive(channel));
		s.setSubscribers(new HashMap<String, String>());
		JsonDB.database.upsert(s);
		
		DiscordUtils.giveRole(event, discordId, DiscordBot.configuration.getStreamRoleId());
		DiscordUtils.sendMessage(event, String.format("""
				```yaml
				Successfully Linked! | User: %s | TwitchChannel: %s | affiliate: %s
				```
				""", "<@"+discordId+">", channel, affiliate), false);
		
		// Restart twitch bot to register new channel listener
		TwitchCommandRestart.execute(event);
	}
}
