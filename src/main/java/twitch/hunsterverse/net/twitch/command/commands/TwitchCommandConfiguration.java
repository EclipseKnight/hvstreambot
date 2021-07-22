package twitch.hunsterverse.net.twitch.command.commands;

import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.twitch.TwitchBot;
import twitch.hunsterverse.net.twitch.TwitchUtils;
import twitch.hunsterverse.net.twitch.command.TwitchCommand;
import twitch.hunsterverse.net.twitch.command.TwitchCommandEvent;

public class TwitchCommandConfiguration extends TwitchCommand {
	
	public TwitchCommandConfiguration() {
		this.feature = "twitch_command_configuration";
		this.name = TwitchBot.configuration.getFeatures().get(feature).getName();
		this.aliases = TwitchBot.configuration.getFeatures().get(feature).getAliases();
	}

	@Override
	protected void execute(TwitchCommandEvent event) {
		if (TwitchUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
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
