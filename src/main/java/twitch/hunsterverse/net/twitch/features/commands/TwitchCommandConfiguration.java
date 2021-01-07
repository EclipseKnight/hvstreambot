package twitch.hunsterverse.net.twitch.features.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import twitch.hunsterverse.net.twitch.TwitchBot;

public class TwitchCommandConfiguration {

	public static void execute(ChannelMessageEvent event) {
		String msg = event.getMessage();
		String[] args = msg.substring(msg.indexOf("!c")+3).split("\\s+");
		
		if (args.length <= 1) {
			event.getTwitchChat().sendMessage(event.getChannel().getName(), "Invalid Arguments: Use [list, ls] or [reload].");
			return;
		}
		
		if ("ls".equals(args[1]) || "list".equals(args[1])) {
			event.getTwitchChat().sendMessage(event.getChannel().getName(), TwitchBot.configuration.toString());
			return;
		}
		
		if ("reload".equals(args[1])) {
			TwitchBot.loadConfiguration();
			event.getTwitchChat().sendMessage(event.getChannel().getName(), "Configuration reloaded...\n" + TwitchBot.configuration.toString());
			return;
		}
	}
}
