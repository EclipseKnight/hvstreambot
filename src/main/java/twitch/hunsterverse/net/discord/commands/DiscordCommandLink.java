package twitch.hunsterverse.net.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.database.HVStreamer;
import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.twitch.commands.TwitchCommandRestart;
import twitch.hunsterverse.net.twitch.features.TwitchAPI;

public class DiscordCommandLink extends Command {

	String feature = "discord_command_link";
	public DiscordCommandLink() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases = DiscordBot.configuration.getFeatures().get(feature).getAliases();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) 
			return;
		
		// get args.
		String[] args = CommandUtils.splitArgs(event.getArgs());
		
		if (args.length < 3) {
			DiscordUtils.sendTimedMessaged(event, "Invalid Arguments: link <@discorduser> <twitchchannel> [<affiliate> true, false]", 5000, false);
			return;
		}
			
		
		String discordId = CommandUtils.getIdFromMention(args[0]);
		if (!CommandUtils.isValidSnowflake(discordId)) {
			DiscordUtils.sendTimedMessaged(event, "Invalid Snowflake.", 5000, false);
			return;
		}
		
		String channel = args[1].trim();
		if (!TwitchAPI.isChannel(channel)) {
			DiscordUtils.sendTimedMessaged(event, "Channel does not exist.", 5000, false);
			return;
		}
		
		boolean affiliate = Boolean.valueOf(args[2]);

		// If user is already linked.
		if (CommandUtils.getUserWithDiscordId(discordId) != null && CommandUtils.getUserWithDiscordId(discordId).isLinked()) {
			HVStreamer s = CommandUtils.getUserWithDiscordId(discordId);
			s.setLinked(true);
			s.setTwitchChannel(channel);
			s.setAffiliate(affiliate);
			JsonDB.database.upsert(s);
			
			DiscordUtils.giveRole(event, discordId, DiscordBot.configuration.getStreamRoleId());
			DiscordUtils.sendMessage(event, String.format("""
					```yaml
					Successfully Relinked! | User: %s | TwitchChannel: %s | affiliate: %s
					```
					""", "<@"+discordId+">", channel, affiliate), false);
			
			// Restart twitch bot to register new channel listener
			TwitchCommandRestart.execute(event);
			return;
		}
		
		
		//New user
		HVStreamer s = new HVStreamer();
		s.setDiscordId(discordId);
		s.setDiscordName(DiscordBot.jda.retrieveUserById(discordId).complete().getAsTag());
		s.setTwitchChannel(channel);
		s.setLinked(true);
		s.setAffiliate(affiliate);
		s.setPingable(false);
		s.setStreaming(TwitchAPI.isLive(channel));
		JsonDB.database.upsert(s);
		
		DiscordUtils.giveRole(event, discordId, DiscordBot.configuration.getStreamRoleId());
		DiscordUtils.sendMessage(event, String.format("""
				```yaml
				Successfully Linked! | User: %s | TwitchChannel: %s | affiliate: %s
				```
				""", "<@"+discordId+">", channel, affiliate), false);
		
		// Restart twitch bot to register new channel listener
		TwitchCommandRestart.execute(event);
	}
}
