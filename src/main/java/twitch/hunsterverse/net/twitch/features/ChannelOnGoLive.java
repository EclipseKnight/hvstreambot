package twitch.hunsterverse.net.twitch.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.events.ChannelGoLiveEvent;

public class ChannelOnGoLive {

	public ChannelOnGoLive(SimpleEventHandler eventHandler) {
		eventHandler.onEvent(ChannelGoLiveEvent.class, event -> onGoLive(event));
		
	}

	public void onGoLive(ChannelGoLiveEvent event) {
		System.out.println(event.getStream().getUserName() + " is now live.");
	}
	
	
}
