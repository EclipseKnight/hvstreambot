package twitch.hunsterverse.net.discord.commands.subscription;

import java.util.HashMap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVUser;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;

public class DiscordCommandToggleNotifs extends Command {

	final String feature = "discord_command_toggle_notifs";
	public DiscordCommandToggleNotifs() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases = DiscordBot.configuration.getFeatures().get(feature).getAliases();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		HVUser u = CommandUtils.getUserWithDiscordId(event.getAuthor().getId());
		if (u == null) {
			u = new HVUser();
			u.setDiscordId(event.getAuthor().getId());
			u.setDiscordName(event.getAuthor().getAsTag());
			u.setNotifsMuted(false);
			u.setSubscriptions(new HashMap<String, String>());
		}
		
		u.setNotifsMuted(!u.isNotifsMuted());
		
		JsonDB.database.upsert(u);
		
		if (u.isNotifsMuted()) {
			DiscordUtils.sendTimedMessage(event, """
					```yaml
					Notifications are now muted. 
					```
					""", 10000, false);
		} else {
			DiscordUtils.sendTimedMessage(event, """
					```yaml
					Notifications are now un-muted. 
					```
					""", 10000, false);
		}
		
	}

}
