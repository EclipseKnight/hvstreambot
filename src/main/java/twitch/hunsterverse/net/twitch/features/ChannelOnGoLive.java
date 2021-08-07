package twitch.hunsterverse.net.twitch.features;

import java.time.Instant;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.helix.domain.Stream;

import net.dv8tion.jda.api.EmbedBuilder;
import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.database.documents.HVStreamerConfig;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;
import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;
import twitch.hunsterverse.net.twitch.TwitchUtils;

public class ChannelOnGoLive {
	
	public ChannelOnGoLive(SimpleEventHandler eventHandler) {
		eventHandler.onEvent(ChannelGoLiveEvent.class, event -> onGoLive(event));
	}
	
	/**
	 * Fires when one of the linked streamers go live.
	 * @param event
	 */
	public void onGoLive(ChannelGoLiveEvent event) {
		
		EventChannel channel = event.getChannel();
		Stream stream = event.getStream();
		
		Boolean pres = TwitchAPI.recentlyOffline.getIfPresent(channel.getId());
		
		//Log event.
		Logger.log(Level.INFO, stream.getUserName() + " is now live.");
		
		// Set user to streaming.
		HVStreamer s = CommandUtils.getStreamerWithTwitchChannel(channel.getName());
//		s.setTimeStreamed(s.getTimeStreamed() - DiscordBot.metricsTask.getUnprocessedTime());
		s.setStreaming(true);
		JsonDB.database.upsert(s);
		
		HVStreamerConfig config = CommandUtils.getStreamerConfigWithDiscordId(s.getDiscordId());
		//Check if the game played passes the filter set (is contained in the filter list).
		Boolean passFilter = "all_games".equals(config.getSelectedFilter()) || config.getGameFilters().get(config.getSelectedFilter()).contains(stream.getGameName());
		
		//Log to channel for troubleshooting. 
		EmbedBuilder eb = new EmbedBuilder();
		eb.setDescription("<a:livesmall:848591733658615858> **" + stream.getUserName() + "** is now `live`.\n"
				+ "Game: " + stream.getGameName() + "\n"
				+ "Filter: " + config.getSelectedFilter() + "\n"
				+ "Pass Filter?: " + passFilter)
			.setTimestamp(Instant.now())
			.setFooter("Go Live")
			.setColor(DiscordBot.COLOR_SUCCESS);
		DiscordUtils.sendMessage(DiscordBot.configuration.getDatabase().get("backup_log_channel"), eb.build());
		
		
		//If a new streamer went live then highlight channel.
		Logger.log(Level.INFO, passFilter + ":" + !(pres != null && pres));
		
		// Update bot streamer count.
		DiscordUtils.setBotStatus((TwitchUtils.getLiveFilteredChannels().size()) + " streamer(s)");
		
		if (passFilter && !(pres != null && pres)) {
			DiscordUtils.updateLiveEmbeds(false);
			
			//Notify subscribers. 
			DiscordUtils.notifySubscribers(s.getDiscordId(), stream);
			return;
		}
		
		DiscordUtils.updateLiveEmbeds(true);
	}
	
	
}
