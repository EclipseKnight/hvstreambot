package twitch.hunsterverse.net.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import twitch.hunsterverse.net.database.documents.HVStreamer;
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
		
		if (event.getArgs().isEmpty()) {
			DiscordUtils.sendTimedMessage(event, DiscordUtils.createShortEmbed("Invalid Arguments.", 
					DiscordBot.PREFIX + "check [<@discorduser> OR <twitchchannel>",
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
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("Check Results..");
				eb.addField("User:", "`<@"+discordId+">`", false);
				eb.addField("Twitch Channel:", s.getTwitchChannel(), true);
				eb.addField("HV Affiliate:", s.isAffiliate() + "", true);
				eb.addField("Linked:", s.isLinked() + "", true);
				eb.addField("Time Streamed:", "`" + CommandUtils.getTimedStreamedReadable(s.getTimeStreamed()) + "`", false);
				eb.setColor(DiscordBot.COLOR_STREAMER);
				
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

				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("Check Resules..");
				eb.addField("User:", "<@"+discordId+">", true);
				eb.addField("Twitch Channel:", s.getTwitchChannel(), true);
				eb.addBlankField(false);
				eb.addField("HV Affiliate:", s.isAffiliate() + "", true);
				eb.addField("Linked:", s.isLinked() + "", true);
				eb.setColor(DiscordBot.COLOR_STREAMER);
				
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
