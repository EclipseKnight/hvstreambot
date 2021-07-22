package twitch.hunsterverse.net.twitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.helix.domain.Stream;

import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.database.documents.HVStreamerConfig;
import twitch.hunsterverse.net.discord.commands.CommandUtils;
import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;
import twitch.hunsterverse.net.twitch.command.TwitchCommandEvent;
import twitch.hunsterverse.net.twitch.features.TwitchAPI;

public class TwitchUtils {

	public static boolean fullUsageCheck(TwitchCommandEvent event, String feature) {
		boolean result = true;
		String reply = "";
		
		
		if (!isFeatureEnabled(feature)) {
			reply += "Command is disabled. ";
			result = false;
		}
		
		if (!isAffiliate(event, feature)) {
			reply += "Command requires you to be linked. ";
			result = false;
		}
		
		if (!isModOnly(event, feature)) {
			reply += "Command requires you to be mod. ";
			result = false;
		}
		
		if (!reply.isEmpty()) {
			TwitchAPI.sendMessage(event, reply);
		}
		
		return result;
	}

	public static boolean isFeatureEnabled(String feature) {
		return TwitchBot.configuration.getFeatures().get(feature).isEnabled();
	}

	public static boolean isAffiliate(TwitchCommandEvent event, String feature) {
		if (TwitchBot.configuration.getFeatures().get(feature).isAffiliate()) {
			
			if (CommandUtils.getStreamerWithTwitchChannel(event.getChannel().getName()) == null) {
				return false;
			}
			
			if (CommandUtils.getStreamerWithTwitchChannel(event.getChannel().getName()).isAffiliate()) {
				return true;
			}
			return false;
			
		} else {
			return true;
		}
	}

	public static boolean isModOnly(TwitchCommandEvent event, String feature) {
		if (TwitchBot.configuration.getFeatures().get(feature).isModOnly()) {
			if (event.getPermissions().contains(CommandPermission.MODERATOR)) {
				return true;
			}
			
			return false;
			
		} else {
			return true;
		}
	}
	
	
	public static String getTwitchChannelUrl(String channel) {
		return "https://www.twitch.tv/" + channel;
	}
	
	
	/**
	 * Gets a list of the HV streamers to listen to for events.
	 * @return
	 */
	public static List<String> getListenerChannels() {
		List<String> channels = new ArrayList<>();
		
		for(HVStreamer s: JsonDB.database.getCollection(HVStreamer.class)) {
			if (s.isLinked())
				channels.add(s.getTwitchChannel());
		}
		
		return channels;
	}
	
	/**
	 * Gets a list of the live HV streamers.
	 * @return
	 */
	public static List<String> getLiveChannels() {
		List<String> channels = new ArrayList<>();
		
		for(HVStreamer s: JsonDB.database.getCollection(HVStreamer.class)) {
			
			if (s.isLinked() && TwitchAPI.isLive(s.getTwitchChannel()))
				channels.add(s.getTwitchChannel());
		}
		
		return channels;
	}
	
	/**
	 * Gets a list of the live HV streamers who are streaming a game allowed by their selected filter.
	 * @return
	 */
	public static List<String> getLiveFilteredChannels() {
		List<String> channels = new ArrayList<>();
		
		for(HVStreamer s: JsonDB.database.getCollection(HVStreamer.class)) {
			HVStreamerConfig config = CommandUtils.getStreamerConfigWithDiscordId(s.getDiscordId());
			if (config == null) {
				config = new HVStreamerConfig();
				config.setDiscordId(s.getDiscordId());
				config.setSelectedFilter("hv_games");
				config.setGameFilters(new HashMap<String, List<String>>());
				config.setGameFilters(CommandUtils.addDefaultFilters(new HashMap<String, List<String>>()));
				JsonDB.database.upsert(config);
			}
			
			if (s.isLinked() && TwitchAPI.isLive(s.getTwitchChannel())) {
				
				Stream stream = null;
				int pass = 0;
				while (stream == null && pass < 3) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						Logger.log(Level.WARN, "Failed to sleep/interrupt thread.");
					}
					stream = TwitchAPI.getTwitchStream(s.getTwitchChannel());
					pass++;
				}
				
				
				if ("all_games".equals(config.getSelectedFilter()) || config.getGameFilters().get(config.getSelectedFilter()).contains(stream.getGameName())) {
					channels.add(s.getTwitchChannel());
				}
			}
			
		}
		
		return channels;
	}
}
