package twitch.hunsterverse.net.twitch.features;

import java.time.Instant;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.events.ChannelGoOfflineEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.discord.DiscordBot;
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
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setDescription(":black_circle: **" + event.getChannel().getName() + "** is now `offline`.");
		eb.setTimestamp(Instant.now());
		eb.setFooter("Go Offline");
		eb.setColor(DiscordBot.COLOR_FAILURE);
		DiscordUtils.sendMessage(DiscordBot.configuration.getDatabase().get("backup_log_channel"), eb.build());
		
		HVStreamer s = CommandUtils.getStreamerWithTwitchChannel(event.getChannel().getName());
//		s.setTimeStreamed(s.getTimeStreamed() + DiscordBot.metricsTask.getUnprocessedTime());
		s.setStreaming(false);
		JsonDB.database.upsert(s);
		
		DiscordUtils.setBotStatus((TwitchUtils.getLiveFilteredChannels().size()) + " streamer(s)");
		DiscordUtils.updateLiveEmbeds(true);
		
	}
	
	
}
