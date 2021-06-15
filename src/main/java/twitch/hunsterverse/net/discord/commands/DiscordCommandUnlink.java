package twitch.hunsterverse.net.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.twitch.commands.TwitchCommandRestart;

public class DiscordCommandUnlink extends Command {

	final String feature = "discord_command_unlink";
	public DiscordCommandUnlink() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases = DiscordBot.configuration.getFeatures().get(feature).getAliases();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		String[] args = CommandUtils.splitArgs(event.getArgs());
		
		if (event.getArgs().isBlank()) {
			DiscordUtils.sendTimedMessage(event, """
					```yaml
					Invalid Arguments: unlink <discordId or mention>.
					```
					""", 10000, false);
			return;
		}
		
		String discordId = CommandUtils.getIdFromMention(args[0]);
		if (CommandUtils.getStreamerWithDiscordId(discordId) != null && CommandUtils.getStreamerWithDiscordId(discordId).isLinked()) {
			
			HVStreamer s = CommandUtils.getStreamerWithDiscordId(discordId);
			s.setLinked(false);
			s.setAffiliate(false);
			JsonDB.database.upsert(s);
			
			DiscordUtils.removeRole(event, discordId, DiscordBot.configuration.getStreamRoleId());
			DiscordUtils.sendMessage(event, String.format("""
					```yaml
					Successfully Unlinked! | User: %s | TwitchChannel: %s | affiliate: %s
					```
					""", "<@"+discordId+">", s.getTwitchChannel(), s.isAffiliate()), false);
			
			TwitchCommandRestart.execute(event);
		} else {
			DiscordUtils.sendTimedMessage(event, """
					```yaml
					User is not linked.
					```
					""", 10000, false);
		}
		
	}

	
}
