package twitch.hunsterverse.net.twitch.commands;

import java.util.Arrays;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import twitch.hunsterverse.net.twitch.features.TwitchAPI;

public class TwitchCommandIsLive {

	public static void execute(ChannelMessageEvent event) {
		String msg = event.getMessage();
		String[] args = msg.substring(msg.indexOf("!c")+3).split("\\s+");
		
		String result = "Command Result: ";
		if (args.length > 1) {
			result += Arrays.toString(TwitchAPI.isLive(Arrays.asList(Arrays.copyOfRange(args, 1, args.length))));
		}  else {
			result += String.valueOf(TwitchAPI.isLive(args[1])); 
		}
		
		event.getTwitchChat().sendMessage(event.getChannel().getName(), result);
	}
}
