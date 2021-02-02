package twitch.hunsterverse.net.twitch;

import java.util.ArrayList;
import java.util.List;

import twitch.hunsterverse.net.database.HVStreamer;
import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.twitch.features.TwitchAPI;

public class TwitchUtils {

	
	public static String getTwitchChannelUrl(String channel) {
		return "https://www.twitch.tv/" + channel;
	}
	
	public static List<String> getListenerChannels() {
		List<String> channels = new ArrayList<>();
		
		for(HVStreamer s: JsonDB.database.getCollection(HVStreamer.class)) {
			if (s.isLinked())
				channels.add(s.getTwitchChannel());
		}
		
		return channels;
	}
	
	public static List<String> getLiveChannels() {
		List<String> channels = new ArrayList<>();
		
		for(HVStreamer s: JsonDB.database.getCollection(HVStreamer.class)) {
			if (s.isLinked() && TwitchAPI.isLive(s.getTwitchChannel()))
				channels.add(s.getTwitchChannel());
		}
		
		return channels;
	}
}
