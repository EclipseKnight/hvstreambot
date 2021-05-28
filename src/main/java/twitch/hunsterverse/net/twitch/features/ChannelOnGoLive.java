package twitch.hunsterverse.net.twitch.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.helix.domain.Stream;

import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;
import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;
import twitch.hunsterverse.net.twitch.TwitchUtils;

public class ChannelOnGoLive {

	String feature = "discord_message_relay";
	
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
		
		if (TwitchAPI.recentlyOffline.getIfPresent(channel.getId()) == true) return;
		
		Logger.log(Level.INFO, stream.getUserName() + " is now live.");
		
		// Set user to streaming.
		HVStreamer s = CommandUtils.getUserWithTwitchChannel(channel.getName());
		s.setStreaming(true);
		JsonDB.database.upsert(s);
		
		// Update bot streamer count.
		DiscordUtils.setBotStatus((TwitchUtils.getLiveChannels().size()+1) + " streamer(s)");
		
		DiscordUtils.updateLiveEmbeds(false);
	}
}
