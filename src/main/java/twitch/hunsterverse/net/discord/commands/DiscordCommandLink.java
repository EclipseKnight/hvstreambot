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
import twitch.hunsterverse.net.twitch.commands.TwitchCommandRestart;
import twitch.hunsterverse.net.twitch.features.TwitchAPI;

public class DiscordCommandLink extends Command {

	final String feature = "discord_command_link";
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
			DiscordUtils.sendTimedMessage(event, 
					DiscordUtils.createShortEmbed("Invalid Arguments.",
							DiscordBot.PREFIX + "link <@discorduser> <twitchchannel> [<affiliate> true, false]",
							DiscordBot.COLOR_FAILURE), 10000, false);
			return;
		}
			
		
		String discordId = CommandUtils.getIdFromMention(args[0]);
		if (!CommandUtils.isValidSnowflake(discordId)) {
			DiscordUtils.sendTimedMessage(event, DiscordUtils.createShortEmbed("Error: Invalid snowflake.",
					null,
					DiscordBot.COLOR_FAILURE), 10000, false);
			return;
		}
		
		String channel = args[1].trim();
		if (!TwitchAPI.isChannel(channel)) {
			DiscordUtils.sendTimedMessage(event, DiscordUtils.createShortEmbed("Error: Channel does not exist.",
					null,
					DiscordBot.COLOR_FAILURE), 10000, false);
			return;
		}
		
		boolean affiliate = Boolean.valueOf(args[2]);

		// If streamer is already linked.
		if (CommandUtils.getStreamerWithDiscordId(discordId) != null && CommandUtils.getStreamerWithDiscordId(discordId).isLinked()) {
			HVStreamer s = CommandUtils.getStreamerWithDiscordId(discordId);
			s.setLinked(true);
			s.setTwitchChannel(channel);
			s.setAffiliate(affiliate);
			
			JsonDB.database.upsert(s);
			
			if (affiliate) {
				DiscordUtils.giveRole(event, discordId, DiscordBot.configuration.getStreamRoleId());
				DiscordUtils.giveRole(event, discordId, DiscordBot.configuration.getStreamAffiliateRoleId());
			} else {
				DiscordUtils.removeRole(event, discordId, DiscordBot.configuration.getStreamAffiliateRoleId());
				DiscordUtils.giveRole(event, discordId, DiscordBot.configuration.getStreamRoleId());
			}
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Successfully Relinked!");
			eb.addField("User:", "<@"+discordId+">", true);
			eb.addField("Twitch Channel:", channel, true);
			eb.addField("HV Affiliate:", affiliate + "", true);
			eb.addField("Linked:", s.isLinked() + "", true);
			eb.setColor(DiscordBot.COLOR_SUCCESS);
			
			DiscordUtils.sendMessage(event, eb.build(), false);
			
			// Restart twitch bot to register new channel listener
			TwitchCommandRestart.execute(event);
			
			//set default filter.
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
			
			return;
		}
		
		
		//New streamer
		HVStreamer s = new HVStreamer();
		s.setDiscordId(discordId);
		s.setDiscordName(DiscordBot.jda.retrieveUserById(discordId).complete().getAsTag());
		s.setTwitchChannel(channel);
		s.setLinked(true);
		s.setAffiliate(affiliate);
		s.setPingable(false);
		s.setStreaming(TwitchAPI.isLive(channel));
		s.setSubscribers(new HashMap<String, String>());
		JsonDB.database.upsert(s);
		
		DiscordUtils.giveRole(event, discordId, DiscordBot.configuration.getStreamRoleId());
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Successfully Linked!");
		eb.addField("User:", "<@"+discordId+">", true);
		eb.addField("Twitch Channel:", channel, true);
		eb.addField("HV Affiliate:", affiliate + "", true);
		eb.addField("Linked:", s.isLinked() + "", true);
		eb.setColor(DiscordBot.COLOR_SUCCESS);
		
		DiscordUtils.sendMessage(event, eb.build(), false);
		
		// rejoin listener channels to add new channel.
		TwitchBot.rejoinListenerChannels();
		
		// Set appropriate roles.
		if (affiliate) {
			DiscordUtils.giveRole(event, discordId, DiscordBot.configuration.getStreamRoleId());
			DiscordUtils.giveRole(event, discordId, DiscordBot.configuration.getStreamAffiliateRoleId());
		} else {
			DiscordUtils.removeRole(event, discordId, DiscordBot.configuration.getStreamAffiliateRoleId());
			DiscordUtils.giveRole(event, discordId, DiscordBot.configuration.getStreamRoleId());
		}
		
		//set default filter.
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
	}
}
