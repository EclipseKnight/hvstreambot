package twitch.hunsterverse.net.twitch.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class WriteChannelChatToConsole {

    /**
     * Register events of this class with the EventManager/EventHandler
     *
     * @param SimpleEventHandler eventHandler 
     */
    public WriteChannelChatToConsole(SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, event -> onChannelMessage(event));
    }

    /**
     * Subscribe to the ChannelMessage Event and write the output to the console
     */
    public void onChannelMessage(ChannelMessageEvent event) {
        System.out.printf(
                "Channel [%s] - User[%s] - ID[%s] - Message [%s]%n",
                event.getChannel().getName(),
                event.getUser().getName(),
                event.getUser().getId(),
                event.getMessage()
        );
    }

}
