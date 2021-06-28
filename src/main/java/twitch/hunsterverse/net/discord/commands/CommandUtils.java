package twitch.hunsterverse.net.discord.commands;

import java.util.ArrayList;
import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Role;
import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.database.documents.HVUser;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;

public class CommandUtils {

	
	/**
	 * Performs a full permission check.
	 * @param event
	 * @param feature
	 * @return
	 */
	public static boolean fullUsageCheck(CommandEvent event, String feature) {
		boolean result = true;
		String reply = "";
		
		//bypass if the user is an owner or co-owner.
		if (isOwner(event)) {
			return true;
		}
		
		if (!isFeatureEnabled(feature)) {
			reply += "Command is disabled. ";
			result = false;
		}
		
		if (!isFeatureLinked(event.getAuthor().getId(), feature)) {
			reply += "Command requires you to be linked. ";
			result = false;
		}
		
		if (!isAffiliateFeature(event.getAuthor().getId(), feature)) {
			reply += "Command requires you to be an affiliate. ";
			result = false;
		}
		
		if (!correctChannel(event, feature)) {
			reply += "Command is disabled in this channel. ";
			result = false;
		}
		
		if (!canUseCommand(event, feature)) {
			reply += "You are not allowed to use this command. ";
			result = false;
		}
		
		if (!reply.isEmpty()) {
			reply += "\n```";
			reply = "```yaml\n".concat(reply);
			DiscordUtils.sendTimedMessage(event, reply, 10000, false);
		}
		
		return result;
	}
	
	//is user owner or co-owner.
	public static boolean isOwner(CommandEvent event) {
		String userId = event.getMember().getId();
		if (userId.equals(DiscordBot.configuration.getOwnerId())) {
			return true;
		}
		
		for (String id: DiscordBot.configuration.getCoOwnerIds()) {
			if (userId.equals(id)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the feature is enabled.
	 * @param feature
	 * @return
	 */
	public static boolean isFeatureEnabled(String feature) {
		return DiscordBot.configuration.getFeatures().get(feature).isEnabled();
	}
	
	
	/**
	 * Checks if command is allowed in used channel.
	 * @param event
	 * @param feature
	 * @return
	 */
	public static boolean correctChannel(CommandEvent event, String feature) {
		List<String> channels = DiscordBot.configuration.getFeatures().get(feature).getChannels();
		
		if (channels == null || channels.get(0) == null) {
			return true;
		}
		
		if (channels.contains(event.getChannel().getId())) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if user is linked.
	 * @param discordId
	 * @param feature
	 * @return
	 */
	public static boolean isFeatureLinked(String discordId, String feature) {
		
		if (DiscordBot.configuration.getFeatures().get(feature).isLinked()) {
			
			if (getStreamerWithDiscordId(discordId) == null) {
				return false;
			}
			
			if (getStreamerWithDiscordId(discordId).isLinked()) {
				return true;
			}
			return false;
			
		} else {
			return true;
		}
		
	}
	
	public static boolean isAffiliateFeature(String discordId, String feature) {
		
		if (DiscordBot.configuration.getFeatures().get(feature).isAffiliate()) {
			
			if (getStreamerWithDiscordId(discordId) == null) {
				return false;
			}
			
			if (getStreamerWithDiscordId(discordId).isAffiliate()) {
				return true;
			}
			return false;
			
		} else {
			return true;
		}
	}
	
	/**
	 * Checks if the user can use the command based on roles.
	 * @param event
	 * @param feature
	 * @return
	 */
	public static boolean canUseCommand(CommandEvent event, String feature) {
		
		List<String> cmdRoles = DiscordBot.configuration.getFeatures().get(feature).getRoles();
		
		if (cmdRoles == null || cmdRoles.get(0) == null) {
			return true;
		}
		
		List<String> sRoles = new ArrayList<>();
		
		for (Role r : event.getMember().getRoles()) {
			sRoles.add(r.getId());
		}
		
		for (String pRole: sRoles) {
			for (String cmdRole: cmdRoles) {
				if (pRole.equals(cmdRole)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static HVStreamer getStreamerWithDiscordId(String id) {
		if (id == null) {
			return null;
		}
		
		String jxQuery = String.format("/.[discordId='%s']", id);
		List<HVStreamer> streamers = JsonDB.database.find(jxQuery, HVStreamer.class);
		
		if (!streamers.isEmpty() && streamers != null) {
			return streamers.get(0);
		}
		
		return null;
	}
	
	public static HVStreamer getStreamerWithDiscordName(String effname) {
		if (effname == null) {
			return null;
		}
		
		effname = effname.toLowerCase();
		String jxQuery = String.format("/.[translate(discordEffName, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='%s']", effname);
		List<HVStreamer> streamers = JsonDB.database.find(jxQuery, HVStreamer.class);
		
		if (!streamers.isEmpty() && streamers != null) {
			return streamers.get(0);
		}
		
		return null;
	}
	
	public static HVStreamer getStreamerWithTwitchChannel(String channel) {
		if (channel == null) {
			return null;
		}
		
		channel = channel.toLowerCase();
		String jxQuery = String.format("/.[translate(twitchChannel, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='%s']", channel);
		List<HVStreamer> streamers = JsonDB.database.find(jxQuery, HVStreamer.class);
		
		if (!streamers.isEmpty() && streamers != null) {
			return streamers.get(0);
		}
		
		return null;
	}
	
	public static HVUser getUserWithDiscordId(String id) {
		if (id == null) {
			return null;
		}
		
		String jxQuery = String.format("/.[discordId='%s']", id);
		List<HVUser> users = JsonDB.database.find(jxQuery, HVUser.class);
		
		if (!users.isEmpty() && users != null) {
			return users.get(0);
		}
		
		return null;
	}
	
	public static HVUser getUserWithDiscordName(String effname) {
		if (effname == null) {
			return null;
		}
		
		effname = effname.toLowerCase();
		String jxQuery = String.format("/.[translate(discordName, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='%s']", effname);
		List<HVUser> users = JsonDB.database.find(jxQuery, HVUser.class);
		
		if (!users.isEmpty() && users != null) {
			return users.get(0);
		}
		
		return null;
	}
	
	
	public static String[] splitArgs(String args) {
		return args.split("\\s+");
	}
	
	public static String getIdFromMention(String mention) {
		return mention.replaceAll("[\\\\<>@#&!]", "");
	}
	
	public static boolean isValidSnowflake(String input) {
		if (input.isEmpty()) {
			return false;
		}
		
        try {
            if (!input.startsWith("-")) // if not negative -> parse unsigned
                Long.parseUnsignedLong(input);
            else // if negative -> parse normal
                Long.parseLong(input);
        } catch (NumberFormatException ex) {
//        	Logger.log(Level.WARN, "The specified ID is not a valid snowflake (%s). Expecting a valid long value!" + " ID:" + input);
            return false;
        }
        return true;
	}
	
}
