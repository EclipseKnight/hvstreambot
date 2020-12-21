package twitch.hunsterverse.net.twitch.chatbot.features;

import java.util.Arrays;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import twitch.hunsterverse.net.twitch.chatbot.TwitchBot;

public class ChannelCommandHandler {

	public ChannelCommandHandler(SimpleEventHandler eventHandler) {
		eventHandler.onEvent(ChannelMessageEvent.class, event -> onChannelMessage(event));
	}

	public void onChannelMessage(ChannelMessageEvent event) {
		String msg = event.getMessage().toLowerCase();
		
		/*
		 * check if message is a command attempt. 
		 */
		if(event.getMessage().startsWith("!c ")) {

			/*
			 * Get the arguments.
			 */
			String[] args = msg.substring(msg.indexOf("!c")+3).split("\\s+");
			/*
			 * First argument is always the command name.
			 * So check what command is being used. 
			 */
			System.out.println(Arrays.toString(args));
			
			String cmd = switch (args[0]) {
				case "islive" -> { 
					if (args.length > 1) {
						yield Arrays.toString(TwitchAPI.isLive(Arrays.asList(Arrays.copyOfRange(args, 1, args.length))));
					}  else {
						yield String.valueOf(TwitchAPI.isLive(args[1])); 
					}
				}
				
				default -> throw new IllegalArgumentException("Unexpected value: " + args[0]);
			};
			System.out.println("Command results: " + cmd);
			TwitchBot.twitchClient.getChat().sendMessage(event.getChannel().getName(), "Command results: " + cmd);
		}
	}
}
