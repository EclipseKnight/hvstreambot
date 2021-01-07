package twitch.hunsterverse.net.twitch.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;

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
    	Logger.log(Level.CHAT, String.format(
                "Channel [%s] - User[%s] - ID[%s] - Message [%s]",
                event.getChannel().getName(),
                event.getUser().getName(),
                event.getUser().getId(),
                event.getMessage()
        ));
    }
}
