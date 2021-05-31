package twitch.hunsterverse.net.twitch.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.helix.domain.Stream;

import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamer;
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
		
		if (pres != null && pres == true) {
			Logger.log(Level.INFO, "Found in cache. Stopping event.");
			return;
		}
		
		Logger.log(Level.INFO, stream.getUserName() + " is now live.");
		DiscordUtils.sendMessage(DiscordBot.configuration.getDatabase().get("backup_log_channel"), stream.getUserName() + " is now live.");
		
		// Set user to streaming.
		HVStreamer s = CommandUtils.getUserWithTwitchChannel(channel.getName());
		s.setStreaming(true);
		JsonDB.database.upsert(s);
		
		// Update bot streamer count.
		DiscordUtils.setBotStatus((TwitchUtils.getLiveChannels().size()) + " streamer(s)");
		
		DiscordUtils.updateLiveEmbeds(false);
	}
}
