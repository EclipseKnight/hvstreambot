package twitch.hunsterverse.net.streamapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.twitch4j.helix.domain.Stream;

import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.database.documents.HVStreamerConfig;
import twitch.hunsterverse.net.discord.commands.CommandUtils;
import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;
import twitch.hunsterverse.net.twitch.features.TwitchAPI;
import twitch.hunsterverse.net.youtube.YoutubeAPI;

public class StreamUtils {

	public static List<HVStreamer> getLiveStreamers() {
		List<HVStreamer> streamers = new ArrayList<>();
		
		for (HVStreamer s: JsonDB.database.getCollection(HVStreamer.class)) {
			s.setLiveTwitch(false);
			s.setLiveYoutube(false);
			HVStreamerConfig config = CommandUtils.getStreamerConfigWithDiscordId(s.getDiscordId());
			if (config == null) {
				config = new HVStreamerConfig();
				config.setDiscordId(s.getDiscordId());
				config.setSelectedFilter("hv_games");
				config.setGameFilters(new HashMap<String, List<String>>());
				config.setGameFilters(CommandUtils.addDefaultFilters(new HashMap<String, List<String>>()));
				JsonDB.database.upsert(config);
			}
			
			
			//perform is live checks
			if (!s.isLinked()) {
				continue;
			}
			
			if (s.getTwitchChannel() != null && TwitchAPI.isLive(s.getTwitchChannel())) {
				
				//This block of code is a workaround for twitch's API failing to get stream if called too soon 
				//after user goes live. Blame Twitch's trash API.
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
					s.setLiveTwitch(true);
				}
			}
			
			if (s.getYoutubeChannelId() != null && YoutubeAPI.getChannel(s.getYoutubeChannelId()).isLive()) {
				s.setLiveYoutube(true);
				//TODO implement gamefilter check once able to scrape game being streamed.
			}
			
			if (s.isLiveTwitch() || s.isLiveYoutube()) {
				streamers.add(s);
			}
		}
		
		return streamers;
	}
}
