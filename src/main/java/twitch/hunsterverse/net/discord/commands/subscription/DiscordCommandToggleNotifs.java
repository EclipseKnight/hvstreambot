package twitch.hunsterverse.net.discord.commands.subscription;

import java.util.HashMap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
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
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setFooter("Use '" + DiscordBot.PREFIX + "toggle' - to mute/un-mute all notifications.");
		
		if (u.isNotifsMuted()) {
			eb.setDescription("Notifications are now muted!");
			eb.setColor(DiscordBot.COLOR_FAILURE);
		} else {
			eb.setDescription("Notifications are now un-muted!");
			eb.setColor(DiscordBot.COLOR_SUCCESS);
		}
		
		DiscordUtils.sendMessage(event, eb.build(), false);
		
	}

}
