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
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setDescription("<a:livesmall:848591733658615858> **" + stream.getUserName() + "** is now `live`.");
		eb.setTimestamp(Instant.now());
		eb.setFooter("Go Live");
		eb.setColor(DiscordBot.COLOR_SUCCESS);
		DiscordUtils.sendMessage(DiscordBot.configuration.getDatabase().get("backup_log_channel"), eb.build());
		
		// Set user to streaming.
		HVStreamer s = CommandUtils.getStreamerWithTwitchChannel(channel.getName());
		s.setTimeStreamed(s.getTimeStreamed() - DiscordBot.metricsTask.getUnprocessedTime());
		s.setStreaming(true);
		JsonDB.database.upsert(s);
		
		// Update bot streamer count.
		DiscordUtils.setBotStatus((TwitchUtils.getLiveFilteredChannels().size()) + " streamer(s)");
		
		HVStreamerConfig config = CommandUtils.getStreamerConfigWithDiscordId(s.getDiscordId());
		
		
		//If a new streamer went live then highlight channel.
		System.out.println(("all_games".equals(config.getSelectedFilter()) || config.getGameFilters().get(config.getSelectedFilter()).contains(stream.getGameName())) + ":" + !(pres != null && pres));
		
		if ("all_games".equals(config.getSelectedFilter()) || config.getGameFilters().get(config.getSelectedFilter()).contains(stream.getGameName()) && !(pres != null && pres)) {

			DiscordUtils.updateLiveEmbeds(false);
			
			//Notify subscribers. 
			DiscordUtils.notifySubscribers(s.getDiscordId(), stream);
			return;
		}
		
		DiscordUtils.updateLiveEmbeds(true);
	}
	
	
}
