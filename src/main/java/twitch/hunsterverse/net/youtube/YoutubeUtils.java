package twitch.hunsterverse.net.youtube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.database.documents.HVStreamerConfig;
import twitch.hunsterverse.net.discord.commands.CommandUtils;

public class YoutubeUtils {

	public static List<YTChannel> getLiveChannels() {
		List<YTChannel> channels = new ArrayList<>();
		
		for (HVStreamer s: JsonDB.database.getCollection(HVStreamer.class)) {
			
			//If channel is not linked OR youtube is not linked skip
			if (!s.isLinked() || s.getYoutubeChannelId() == null) {
				continue;
			}
			
			HVStreamerConfig config = CommandUtils.getStreamerConfigWithDiscordId(s.getDiscordId());
			if (config == null) {
				config = new HVStreamerConfig();
				config.setDiscordId(s.getDiscordId());
				config.setSelectedFilter("hv_games");
				config.setGameFilters(new HashMap<String, List<String>>());
				config.setGameFilters(CommandUtils.addDefaultFilters(new HashMap<String, List<String>>()));
				JsonDB.database.upsert(config);
			}
			
			YTChannel user = YoutubeAPI.getChannel(s.getYoutubeChannelId());
			
			if (user.isLive()) {
				channels.add(user);
			}
			
		}
		
		return channels;
	}
}
