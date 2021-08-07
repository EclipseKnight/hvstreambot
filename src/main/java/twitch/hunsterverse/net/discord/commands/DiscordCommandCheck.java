package twitch.hunsterverse.net.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.database.documents.HVStreamerConfig;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.twitch.features.TwitchAPI;

public class DiscordCommandCheck extends Command {

	final String feature = "discord_command_check";
	
	public DiscordCommandCheck() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases = DiscordBot.configuration.getFeatures().get(feature).getAliases();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature))
			return;
		
		String[] args = CommandUtils.splitArgs(event.getArgs());
		
		if (event.getArgs().isBlank()) {
			HVStreamer s = CommandUtils.getStreamerWithDiscordId(event.getAuthor().getId());
			HVStreamerConfig c = CommandUtils.getStreamerConfigWithDiscordId(event.getAuthor().getId());
			if (s != null) {
				EmbedBuilder eb = new EmbedBuilder()
						.setTitle("Check Resules..")
						.addField("User:", "<@"+event.getAuthor().getId()+">", true)
						.addField("Twitch Channel:", s.getTwitchChannel(), true)
						.addBlankField(false)
						.addField("HV Affiliate:", s.isAffiliate() + "", true)
						.addField("Linked:", s.isLinked() + "", true)
						.addField("Filter", c.getSelectedFilter(), true)
						.setColor(DiscordBot.COLOR_STREAMER);
//				eb.addField("Time Streamed:", "`" + CommandUtils.getTimedStreamedReadable(s.getTimeStreamed()) + "`", false);
				
				DiscordUtils.sendMessage(event, eb.build(), false);
				return;
			}
			
			DiscordUtils.sendTimedMessage(event, 
					DiscordUtils.createShortEmbed("Invalid Arguments.", 
							"Channel or user does not exist.",
							DiscordBot.COLOR_FAILURE), 10000, false);
			
			return;
		}
		
		String discordId = CommandUtils.getIdFromMention(args[0]);
		String channel = args[0].trim();
		
		
		
		// Check for valid snowflake
		if (CommandUtils.isValidSnowflake(discordId)) {
			
			// query for snowflake in db and check for active link.
			if (CommandUtils.getStreamerWithDiscordId(discordId) != null) {
				
				HVStreamer s = CommandUtils.getStreamerWithDiscordId(discordId);
				HVStreamerConfig c = CommandUtils.getStreamerConfigWithDiscordId(discordId);
				EmbedBuilder eb = new EmbedBuilder()
						.setTitle("Check Resules..")
						.addField("User:", "<@"+discordId+">", true)
						.addField("Twitch Channel:", s.getTwitchChannel(), true)
						.addBlankField(false)
						.addField("HV Affiliate:", s.isAffiliate() + "", true)
						.addField("Linked:", s.isLinked() + "", true)
						.addField("Filter", c.getSelectedFilter(), true)
						.setColor(DiscordBot.COLOR_STREAMER);
//				eb.addField("Time Streamed:", "`" + CommandUtils.getTimedStreamedReadable(s.getTimeStreamed()) + "`", false);
				
				DiscordUtils.sendMessage(event, eb.build(), false);
				return;
			}
			
			DiscordUtils.sendTimedMessage(event, 
					DiscordUtils.createShortEmbed("Invalid Arguments.", 
							"Channel or user does not exist.",
							DiscordBot.COLOR_FAILURE), 10000, false);
			
			return;
		} 
		
		// Check if channel exists
		if (TwitchAPI.isChannel(channel)) {
			// Query for user linked to the channel.
			if (CommandUtils.getStreamerWithTwitchChannel(channel) != null
					&& CommandUtils.getStreamerWithTwitchChannel(channel).isLinked()) {
				
				HVStreamer s = CommandUtils.getStreamerWithTwitchChannel(channel);
				HVStreamerConfig c = CommandUtils.getStreamerConfigWithDiscordId(discordId);
				EmbedBuilder eb = new EmbedBuilder()
						.setTitle("Check Resules..")
						.addField("User:", "<@"+discordId+">", true)
						.addField("Twitch Channel:", s.getTwitchChannel(), true)
						.addBlankField(false)
						.addField("HV Affiliate:", s.isAffiliate() + "", true)
						.addField("Linked:", s.isLinked() + "", true)
						.addField("Filter", c.getSelectedFilter(), true)
						.setColor(DiscordBot.COLOR_STREAMER);
				
//				eb.addField("Time Streamed:", "`" + CommandUtils.getTimedStreamedReadable(s.getTimeStreamed()) + "`", false);
				
				DiscordUtils.sendMessage(event, eb.build(), false);
				return;
			}
		}
		
		// In case neither conditions are met. 
		DiscordUtils.sendTimedMessage(event, 
				DiscordUtils.createShortEmbed("Invalid Arguments.", 
						"Channel or user does not exist.",
						DiscordBot.COLOR_FAILURE), 10000, false);
		return;
	}

}
