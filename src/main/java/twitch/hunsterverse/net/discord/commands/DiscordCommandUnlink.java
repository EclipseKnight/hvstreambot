package twitch.hunsterverse.net.discord.commands;

import java.util.HashMap;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.database.documents.HVStreamerConfig;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.twitch.TwitchBot;

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
			DiscordUtils.sendTimedMessage(event, 
					DiscordUtils.createShortEmbed("Invalid Arguments.",
							DiscordBot.PREFIX + "unlink <discordId or @discorduser>",
							DiscordBot.COLOR_FAILURE), 10000, false);
			return;
		}
		
		String discordId = CommandUtils.getIdFromMention(args[0]);
		if (CommandUtils.getStreamerWithDiscordId(discordId) != null && CommandUtils.getStreamerWithDiscordId(discordId).isLinked()) {
			
			HVStreamer s = CommandUtils.getStreamerWithDiscordId(discordId);
			s.setLinked(false);
			s.setAffiliate(false);
			JsonDB.database.upsert(s);
			
			//Remove roles.
			DiscordUtils.removeRole(event, discordId, DiscordBot.configuration.getStreamRoleId());
			DiscordUtils.removeRole(event, discordId, DiscordBot.configuration.getStreamAffiliateRoleId());
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Successfully Unlinked!");
			eb.addField("User:", "<@"+discordId+">", true);
			eb.addField("Twitch Channel:", s.getTwitchChannel(), true);
			eb.addField("HV Affiliate:", s.isAffiliate() + "", true);
			eb.addField("Linked:", s.isLinked() + "", true);
			eb.setColor(DiscordBot.COLOR_STREAMER);
			
			DiscordUtils.sendMessage(event, eb.build(), false);
			
			TwitchBot.rejoinListenerChannels();
			
			//Set default filter.
			if (!s.isAffiliate()) {
				HVStreamerConfig config = CommandUtils.getStreamerConfigWithDiscordId(discordId);
				if (config == null) {
					config = new HVStreamerConfig();
					config.setDiscordId(event.getAuthor().getId());
					config.setSelectedFilter("hv_games");
					config.setGameFilters(new HashMap<String, List<String>>());
					config.setGameFilters(CommandUtils.addDefaultFilters(new HashMap<String, List<String>>()));
					JsonDB.database.upsert(config);
				}
				config.setSelectedFilter("hv_games");
			}
			
		} else {
			
			DiscordUtils.sendTimedMessage(event, 
					DiscordUtils.createShortEmbed("Error: User is not linked.",
							null,
							DiscordBot.COLOR_FAILURE), 10000, false);
		}
		
	}

	
}
