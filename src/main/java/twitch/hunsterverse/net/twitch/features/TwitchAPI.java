package twitch.hunsterverse.net.twitch.features;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.helix.domain.GameList;
import com.github.twitch4j.helix.domain.StreamList;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;

import twitch.hunsterverse.net.twitch.TwitchBot;

public class TwitchAPI {

	
	public static final Cache<String, Boolean> recentlyOffline = Caffeine.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).build();
	/**
	 * 
	 * @param username a username to check
	 * @return a boolean. true if live, else false.
	 */
	public static boolean isLive(String username) {
		List<String> users = new ArrayList<String>();
		
		/*
		 * Only alphanumerical and no spaces.
		 */
		if (!username.matches("^[a-zA-Z0-9]*$")) {
			return false; 
		}
		
		users.add(username);
		StreamList resultList = TwitchBot.twitchClient.getHelix().getStreams(null, null, null, 5, null, null, null, users).execute();
		
		
		if (resultList.getStreams().size() > 0 && "live".equals(resultList.getStreams().get(0).getType())) {
//			Logger.log(Level.INFO, resultList.getStreams().get(0).getUserName() + " is live.");
			return true; 
			
		} else {
//			Logger.log(Level.INFO, "No \"Live\" Streams found under the login name \""+ username + "\".");
			return false;
		}
		
	}
	
	/**
	 * 
	 * @param usernames a List of usernames to check.
	 * @return a boolean array indexed based off the passed in list. true if live, else false.
	 */
	public static boolean[] isLive(List<String> usernames) {
		if (!(usernames.size() > 0)) {
			return null;
		}
		
		boolean[] resultList = new boolean[usernames.size()];
		for (int i = 0; i < usernames.size(); i++) {
			resultList[i] = isLive(usernames.get(i));
		}
		
		return resultList;
		
	}
	
	
	/**
	 * Checks if the channel actually exists.
	 * @param channel
	 * @return
	 */
	public static boolean isChannel(String channel) {
		if (channel == null)
			return false;
		
		List<String> user = new ArrayList<>();
		user.add(channel);
		
		UserList list = TwitchBot.twitchClient.getHelix().getUsers(null, null, user).execute();
		if (list.getUsers().size() > 0 && list.getUsers().get(0).getDisplayName().equalsIgnoreCase(channel)) {
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * Utility method to make sending twitch messages cleaner since the library currently has no quick reply method like JDA.
	 * @param event
	 * @param message
	 */
	public static void sendMessage(ChannelMessageEvent event, String message) {
		event.getTwitchChat().sendMessage(event.getChannel().getName(), message);
	}
	
	/**
	 * Gets a twitch user.
	 * @param name
	 * @return
	 */
	public static User getTwitchUser(String name) {
		
		List<String> user = new ArrayList<>();
		user.add(name);
		
		UserList list = TwitchBot.twitchClient.getHelix().getUsers(null, null, user).execute();
		
		if (list.getUsers().size() > 0 && list.getUsers().get(0).getDisplayName().equalsIgnoreCase(name)) {
			return list.getUsers().get(0);
		}
		
		return null;
	}
	
	public static String getGameName(String gameId) {
		List<String> game = new ArrayList<>();
		game.add(gameId);
		
		GameList result = TwitchBot.twitchClient.getHelix().getGames(null, game, null).execute();
		if (result.getGames().size() > 0 && result.getGames().get(0).getId().equals(gameId)) {
			return result.getGames().get(0).getName();
		}
		return null;
	}
}
