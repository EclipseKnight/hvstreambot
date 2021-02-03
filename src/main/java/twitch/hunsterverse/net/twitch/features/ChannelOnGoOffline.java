package twitch.hunsterverse.net.twitch.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.events.ChannelGoOfflineEvent;

import twitch.hunsterverse.net.database.HVStreamer;
import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;
import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;
import twitch.hunsterverse.net.twitch.TwitchUtils;

public class ChannelOnGoOffline {

	public ChannelOnGoOffline(SimpleEventHandler eventHandler) {
		eventHandler.onEvent(ChannelGoOfflineEvent.class, event -> onGoOffline(event));
		
	}

	/**
	 * Fires when one of the linked streamers go offline.
	 * @param event
	 */
	public void onGoOffline(ChannelGoOfflineEvent event) {
		
		TwitchAPI.recentlyOffline.put(event.getChannel().getId(), true);
		
		Logger.log(Level.INFO, event.getChannel().getName() + " is now offline.");
		HVStreamer s = CommandUtils.getUserWithTwitchChannel(event.getChannel().getName());
		s.setStreaming(false);
		JsonDB.database.upsert(s);
		
		DiscordUtils.setBotStatus((TwitchUtils.getLiveChannels().size()) + " streamer(s)");
//		DiscordUtils.sendRelayMessage(s.getDiscordName() + " [" + s.getTwitchChannel() + "]" + " is now offline.");
	}
	
	
}
