package twitch.hunsterverse.net.twitch.command.commands;

import java.util.Arrays;

import twitch.hunsterverse.net.twitch.TwitchBot;
import twitch.hunsterverse.net.twitch.TwitchUtils;
import twitch.hunsterverse.net.twitch.command.TwitchCommand;
import twitch.hunsterverse.net.twitch.command.TwitchCommandEvent;
import twitch.hunsterverse.net.twitch.features.TwitchAPI;

/**
 * Command to execute when isLive command is fired in chat.
 * @author jpaqu
 *
 */
public class TwitchCommandIsLive extends TwitchCommand {
	
	public TwitchCommandIsLive() {
		this.feature = "twitch_command_is_live";
		this.name = TwitchBot.configuration.getFeatures().get(feature).getName();
		this.aliases = TwitchBot.configuration.getFeatures().get(feature).getAliases();
	}

	@Override
	protected void execute(TwitchCommandEvent event) {
		if (!TwitchUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		String[] args = event.getArgs().split("\\s+");
		
		if (event.getArgs().isBlank()) {
			return;
		}
		
		String result = "Command Result: ";
		if (args.length > 1) {
			result += Arrays.toString(TwitchAPI.isLive(Arrays.asList(Arrays.copyOfRange(args, 0, args.length))));
		}  else {
			result += String.valueOf(TwitchAPI.isLive(args[0])); 
		}
		
		event.getTwitchChat().sendMessage(event.getChannel().getName(), result);
	}
}
