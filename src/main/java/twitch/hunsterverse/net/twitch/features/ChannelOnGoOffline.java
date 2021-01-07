package twitch.hunsterverse.net.twitch.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.events.ChannelGoOfflineEvent;

import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;

public class ChannelOnGoOffline {

	public ChannelOnGoOffline(SimpleEventHandler eventHandler) {
		eventHandler.onEvent(ChannelGoOfflineEvent.class, event -> onGoOffline(event));
		
	}

	public void onGoOffline(ChannelGoOfflineEvent event) {
		Logger.log(Level.INFO, event.getChannel().getName() + " is now offline.");
	}
	
	
}
