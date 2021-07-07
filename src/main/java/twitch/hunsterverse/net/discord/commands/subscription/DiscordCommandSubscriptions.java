package twitch.hunsterverse.net.discord.commands.subscription;

import java.util.HashMap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.database.documents.HVUser;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;

public class DiscordCommandSubscriptions extends Command {

	final String feature = "discord_command_subscriptions";
	public DiscordCommandSubscriptions() {
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
		
		if (u.getSubscriptions().size() <= 0) {
			DiscordUtils.sendMessage(event, 
					DiscordUtils.createShortEmbed("Error: You aren't subscribed to anyone...",
							"Use '" + DiscordBot.PREFIX + "subscribe <@discorduser or discordID>' - to subscribe to a HV streamer.",
							DiscordBot.COLOR_FAILURE), true);
			
			return;
		}
		
		String list = "Subscription List\n\n";
		
		for(String key: u.getSubscriptions().keySet()) {
			HVStreamer s = CommandUtils.getStreamerWithDiscordId(key);
			
			list += s.getDiscordName() + ":" + s.getTwitchChannel() + "\n";
		}
		
		DiscordUtils.sendMessage(event, list, true);
	}
	
	
}
