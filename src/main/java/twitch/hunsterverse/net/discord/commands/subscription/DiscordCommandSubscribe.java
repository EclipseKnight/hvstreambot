package twitch.hunsterverse.net.discord.commands.subscription;

import java.util.HashMap;
import java.util.UUID;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.database.documents.HVUser;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;

public class DiscordCommandSubscribe extends Command {

	final String feature = "discord_command_subscribe";
	
	String logChannel = DiscordBot.configuration.getDatabase().get("backup_log_channel");
	
	public DiscordCommandSubscribe() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases = DiscordBot.configuration.getFeatures().get(feature).getAliases();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		String[] args = CommandUtils.splitArgs(event.getArgs());
		
		if (event.getArgs().isBlank()) {
			DiscordUtils.sendTimedMessage(event,
					DiscordUtils.createShortEmbed("Invalid Arguments",
							DiscordBot.PREFIX + "subscribe <@discorduser or discordID>",
							DiscordBot.COLOR_FAILURE), 10000, false);
			return;
		}
		String streamerId = CommandUtils.getIdFromMention(args[0]);
		String userId = event.getAuthor().getId();
		
		
		//Check if user exists and proceed if so.
		HVUser u = CommandUtils.getUserWithDiscordId(userId);
		if (u == null) {
			u = new HVUser();
			u.setDiscordId(userId);
			u.setDiscordName(event.getAuthor().getAsTag());
			u.setNotifsMuted(false);
			u.setSubscriptions(new HashMap<String, String>());
		}
		
		//check if id is valid and streamer exists.
		if (CommandUtils.isValidSnowflake(streamerId)) {
			HVStreamer s = CommandUtils.getStreamerWithDiscordId(streamerId);
			
			if (s == null) {
				DiscordUtils.sendTimedMessage(event,
						DiscordUtils.createShortEmbed("Error: Streamer does not exist in database.", 
								null,
								DiscordBot.COLOR_FAILURE), 10000, false);
				return;
			}
			
			if (!s.isLinked()) {
				DiscordUtils.sendTimedMessage(event, 
						DiscordUtils.createShortEmbed("Error: Streamer is not currently linked to the bot and a channel.", 
								null,
								DiscordBot.COLOR_FAILURE), 10000, false);
				return;
			}
			
			//temp fix for old linked users. //TODO remove once all are updated. 
			if (s.getSubscribers() == null) {
				s.setSubscribers(new HashMap<String, String>());
			}
			
			//Check if user is subscribed to streamer.
			if (u.getSubscriptions().containsKey(streamerId)) {
				DiscordUtils.sendTimedMessage(event, 
						DiscordUtils.createShortEmbed("Error: You are subscribed to this streamer already.", 
								"Use '" + DiscordBot.PREFIX + "subscriptions' - to see who you are subscribed to.",
								DiscordBot.COLOR_FAILURE), 10000, false);
				return;
			}
			
			
			//Generate users uuid
			UUID uuid = JsonDB.gen.generate();
			
			//put in maps
			s.getSubscribers().put(uuid.toString(), userId);
			u.getSubscriptions().put(streamerId, uuid.toString());
			
			JsonDB.database.upsert(s);
			JsonDB.database.upsert(u);
			
			DiscordUtils.sendMessage(event,
					DiscordUtils.createShortEmbed("You have subscribed to " + s.getDiscordName() + "! You will be messaged when they go live.", 
							null,
							DiscordBot.COLOR_SUCCESS), false);
			
			DiscordUtils.sendMessage(logChannel, 
					DiscordUtils.createShortEmbed(u.getDiscordName() + " subscribed to " + s.getDiscordName() + "!", 
							null,
							DiscordBot.COLOR_SUCCESS));
			return;
		}
		
		// In case neither conditions are met. 
		DiscordUtils.sendTimedMessage(event,
				DiscordUtils.createShortEmbed("Invalid Arguments", 
						"Invalid snowflake or streamer does not exist in database.",
						DiscordBot.COLOR_SUCCESS), 10000, false);
		return;
	}

	
}
