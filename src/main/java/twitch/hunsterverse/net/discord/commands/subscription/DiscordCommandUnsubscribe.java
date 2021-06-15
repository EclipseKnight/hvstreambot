package twitch.hunsterverse.net.discord.commands.subscription;

import java.util.HashMap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.database.documents.HVUser;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;

public class DiscordCommandUnsubscribe extends Command {

	final String feature = "discord_command_unsubscribe";
	public DiscordCommandUnsubscribe() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases = DiscordBot.configuration.getFeatures().get(feature).getAliases();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		String[] args = CommandUtils.splitArgs(event.getArgs());
		
		if (args.length < 2) {
			DiscordUtils.sendTimedMessage(event, """
					```yaml
					Invalid Arguments: unsubscribe <@discorduser>
					```
					""", 10000, false);
			return;
		}
		String streamerId = CommandUtils.getIdFromMention(args[0]);
		String userId = event.getAuthor().getId();
		
		
		//Check if user exists and proceed if so, otherwise, exit.
		HVUser u = CommandUtils.getUserWithDiscordId(userId);
		if (u == null) {
			u = new HVUser();
			u.setDiscordId(userId);
			u.setDiscordName(event.getAuthor().getAsTag());
			u.setNotifsMuted(false);
			u.setSubscriptions(new HashMap<String, String>());
			
			JsonDB.database.upsert(u);
			DiscordUtils.sendTimedMessage(event, """
					```yaml
					You have not subscribed to anyone yet.
					```
					""", 10000, false);
			return;
		}
		
		
		
		//check if id is valid and streamer exists.
		if (CommandUtils.isValidSnowflake(streamerId)) {
			HVStreamer s = CommandUtils.getStreamerWithDiscordId(streamerId);
			
			if (s == null) {
				DiscordUtils.sendTimedMessage(event, """
						```yaml
						Invalid Arguments: Streamer does not exist.
						```
						""", 10000, false);
				return;
			}
			
			//temp fix for old linked users. //TODO remove once all are updated. 
			if (s.getSubscribers() == null) {
				s.setSubscribers(new HashMap<String, String>());
			}
			
			
			//Check if user is subscribed to streamer.
			if (!u.getSubscriptions().containsKey(streamerId)) {
				DiscordUtils.sendTimedMessage(event, """
						```yaml
						You are not subscribed to this streamer.
						```
						""", 10000, false);
				return;
			}
			
			//Check if user is in streamers subscriptions.
			if (!s.getSubscribers().containsKey(u.getSubscriptions().get(s.getDiscordId()))) {
				DiscordUtils.sendMessage(event, """
						```yaml
						Could not find user in subscription list. Report this to Eclipse.
						```
						""", false);
				return;
			}
			
			s.getSubscribers().remove(u.getSubscriptions().get(s.getDiscordId()));
			u.getSubscriptions().remove(s.getDiscordId());
			
			JsonDB.database.upsert(s);
			JsonDB.database.upsert(u);
			
			DiscordUtils.sendMessage(event, String.format("""
					```yaml
					You have unsubscribed from %s! You will no longer be messaged when they go live. 
					```
					""", s.getDiscordName()), false);
			return;
		}
		
		// In case neither conditions are met. 
		DiscordUtils.sendTimedMessage(event, """
				```yaml
				Invalid Arguments: invalid snowflake or streamer does not exist.
				```
				""", 10000, false);
		return;
		
	}
}
