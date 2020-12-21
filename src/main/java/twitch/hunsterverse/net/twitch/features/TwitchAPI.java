package twitch.hunsterverse.net.twitch.features;


import java.util.ArrayList;
import java.util.List;

import com.github.twitch4j.helix.domain.StreamList;

import twitch.hunsterverse.net.twitch.TwitchBot;

public class TwitchAPI {

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
			System.out.println(resultList.getStreams().get(0).getUserName() + " is live.");
			return true; 
			
		} else {
			System.out.println("No Live Streams found under the login name \""+ username + "\".");
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
}
