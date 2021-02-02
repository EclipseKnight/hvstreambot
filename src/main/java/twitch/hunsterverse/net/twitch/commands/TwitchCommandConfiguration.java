package twitch.hunsterverse.net.twitch.commands;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.twitch.TwitchBot;

public class TwitchCommandConfiguration {

	public static void execute(ChannelMessageEvent event) {
		String msg = event.getMessage();
		String[] args = msg.substring(msg.indexOf("!c")+3).split("\\s+");
		
		// Invalid arguments
		if (args.length <= 1) {
			event.getTwitchChat().sendMessage(event.getChannel().getName(), "Invalid Arguments: Use [list, ls] or [reload].");
			return;
		}
		
		// List configuration
		if ("ls".equals(args[1]) || "list".equals(args[1])) {
			event.getTwitchChat().sendMessage(event.getChannel().getName(), TwitchBot.configuration.toString());
			return;
		}
		
		// Reload configuration. 
		if ("reload".equals(args[1])) {
			TwitchBot.loadConfiguration();
			DiscordBot.jda.getSelfUser().getManager().setName(DiscordBot.configuration.getBot().get("name"));
			event.getTwitchChat().sendMessage(event.getChannel().getName(), "Configuration reloaded...\n" + TwitchBot.configuration.toString());
			return;
		}
	}
}
