package twitch.hunsterverse.net.twitch.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.events.ChannelGoOfflineEvent;

public class ChannelOnGoOffline {

	public ChannelOnGoOffline(SimpleEventHandler eventHandler) {
		eventHandler.onEvent(ChannelGoOfflineEvent.class, event -> onGoOffline(event));
		
	}

	public void onGoOffline(ChannelGoOfflineEvent event) {
		System.out.println(event.getChannel().getName() + " is now offline.");
	}
	
	
}
