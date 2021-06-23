package twitch.hunsterverse.net.discord.commands;

import java.util.function.Consumer;

import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;

public class DiscordCommandHelp implements Consumer<CommandEvent> {

	@Override
	public void accept(CommandEvent event) {
		String[] commands = new String[] {
				"discord_command_config",
				"discord_command_restart",
				"discord_command_check",
				"discord_command_link",
				"discord_command_unlink",
				"discord_command_is_live",
				"discord_command_embed_update",
				"discord_command_backup",
				"discord_command_subscribe",
				"discord_command_unsubscribe",
				"discord_command_toggle_notifs",
				"discord_command_subscriptions"
				};
		
		String message = "```yaml\nCommands you can use:\n";
		
		for (String c : commands) {
			if (canUse(event, c)) {
				message += DiscordBot.PREFIX
						+ DiscordBot.configuration.getFeatures().get(c).getDescription() + "\n";
			}
		}
		
		message += "```";
		DiscordUtils.sendMessage(event, message, true);
	}
	
	private boolean canUse(CommandEvent event, String feature) {
		boolean result = true;
		
		if (!CommandUtils.isFeatureEnabled(feature)) {
			result = false;
		}
		
		if (!CommandUtils.isFeatureLinked(event.getAuthor().getId(), feature)) {
			result = false;
		}
		
		if (!CommandUtils.isAffiliateFeature(event.getAuthor().getId(), feature)) {
			result = false;
		}
		
		if (!CommandUtils.canUseCommand(event, feature)) {
			result = false;
		}
		
		return result;
	}
}
