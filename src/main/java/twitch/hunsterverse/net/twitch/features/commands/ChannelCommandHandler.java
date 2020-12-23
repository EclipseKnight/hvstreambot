package twitch.hunsterverse.net.twitch.features.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import twitch.hunsterverse.net.twitch.TwitchBot;

public class ChannelCommandHandler {

	public ChannelCommandHandler(SimpleEventHandler eventHandler) {
		eventHandler.onEvent(ChannelMessageEvent.class, event -> onChannelMessage(event));
	}

	public void onChannelMessage(ChannelMessageEvent event) {
		String msg = event.getMessage().toLowerCase();
		
		// check if message is a command attempt. 
		if(event.getMessage().startsWith("!c ")) {

			
			// Get the arguments. 
			String[] args = msg.substring(msg.indexOf("!c")+3).split("\\s+");
			
			
			/*
			 * Because Switch cases can't use non-constant values. I had to go with the less clean if (argument equals command name) approach. 		
			 */
			
			String cmdConfiguration = "config";
			String cmdRestart = "restart";
			String cmdIsLive = TwitchBot.configuration.getFeatures().get("twitch_command_is_live").getName();
			
			
			/*
			 * First argument (args[0]) is always the command name.
			 * So check what command is being used. 
			 */
			
			if (args[0].equals(cmdConfiguration)) {
				TwitchCommandConfiguration.execute(event);
				return;
			}
			
			if (args[0].equals(cmdRestart)) {
				TwitchCommandRestart.execute(event);
				return;
			}
			
			if (args[0].equalsIgnoreCase(cmdIsLive)) {
				TwitchCommandIsLive.execute(event);
				return;
			}
			
			
			
		}
	}
}
