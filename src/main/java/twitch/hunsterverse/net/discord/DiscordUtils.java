package twitch.hunsterverse.net.discord;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.twitch4j.helix.domain.Stream;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.ActiveEmbed;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.discord.commands.CommandUtils;
import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;
import twitch.hunsterverse.net.twitch.TwitchUtils;
import twitch.hunsterverse.net.twitch.features.TwitchAPI;

public class DiscordUtils {
	
	public static void createLiveEmbed() {
		String guildId = DiscordBot.configuration.getGuildId();
		String channelId = DiscordBot.configuration.getLiveEmbedChannel();
		
		ActiveEmbed ae = new ActiveEmbed();
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(DiscordBot.jda.getGuildById(DiscordBot.configuration.getGuildId()).getRoleById(DiscordBot.configuration.getStreamRoleId()).getColorRaw());
		eb.setTitle("Live Hunsterverse Streamers");
		eb.setFooter("Bot created by Eclipse <a:vibecat:743546134584229889>");
		
		//Send the message
		Message m = DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).sendMessage(eb.build()).complete();
		
		//Grab it's id and set it to the active embed doc
		try {
			ae.setMessageId(m.getId());
		} catch (IllegalStateException e) {
			Logger.log(Level.ERROR, "No last message id found.");
		}
		//Upsert to database.
		JsonDB.database.upsert(ae);
	}
	
	public static void updateLiveEmbeds(boolean force) {
		
		Logger.log(Level.WARN, "Updating embeds...");
		
		String guildId = DiscordBot.configuration.getGuildId();
		String channelId = DiscordBot.configuration.getLiveEmbedChannel();
		
		List<ActiveEmbed> aes = JsonDB.database.getCollection(ActiveEmbed.class);
		List<String> liveChannels = TwitchUtils.getLiveChannels();
		
		if (liveChannels.size() <= 0) {
			Logger.log(Level.INFO, "No live channels.");
			
			if (!force) return;
			Logger.log(Level.WARN, "flag set to force. Forcing update...");
		}
		
		
		int fieldCount = liveChannels.size();
		int numOfEmbeds = (int) Math.ceil(fieldCount / 25.0);
		
		if (numOfEmbeds <= 0) numOfEmbeds++;
		
		Logger.log(Level.WARN, numOfEmbeds + " active embeds are needed.");
		
		//adjust active embeds to meet required amount.
		if (numOfEmbeds > aes.size() || numOfEmbeds < aes.size()) {
			
			//If too few, increase
			if (numOfEmbeds > aes.size()) {
				Logger.log(Level.DEBUG, "Too few active embeds ("+aes.size()+"), creating more...");
				
				while (aes.size() < numOfEmbeds) {
					createLiveEmbed();
					aes = JsonDB.database.getCollection(ActiveEmbed.class);
					Logger.log(Level.SUCCESS, "Increased to: " + aes.size());
				}
			}
			
			//If too many, reduce
			if (numOfEmbeds < aes.size()) {
				Logger.log(Level.DEBUG, "Too many active embeds ("+aes.size()+"), reducing amount...");

				while (aes.size() > numOfEmbeds) {
					DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).deleteMessageById(aes.get(aes.size()-1).getMessageId()).queue();
					JsonDB.database.remove(aes.get(aes.size()-1), ActiveEmbed.class);
					aes = JsonDB.database.getCollection(ActiveEmbed.class);
					Logger.log(Level.SUCCESS, "Reduced to: " + aes.size());
				}
			}
		}
		Logger.log(Level.SUCCESS, "Adjusted current active embeds to meet the needed ammount (needed:storedindatabse) ("+numOfEmbeds +":"+aes.size() +").");
		
		//Check each active embed to see if the message still exists. 
		for (ActiveEmbed ae: aes) {
			Message m = null;
			
			//Try to retrieve message. If its unknown, handle the error.
			try {
				m = DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).retrieveMessageById(ae.getMessageId()).complete();
			} catch (ErrorResponseException e) {
				Logger.log(Level.FATAL, "Discord error: " + e.getErrorCode());
				Logger.log(Level.WARN, "10008 indicates unknown message. This is to inform you it is handled.");
			}
			
			// If the message does not exist, remove it and create a new one to meet needed amount.
			if (m == null) {
				JsonDB.database.remove(ae, ActiveEmbed.class);
				createLiveEmbed();
				aes = JsonDB.database.getCollection(ActiveEmbed.class);
				continue;
			}
		}
		
		List<MessageEmbed> updatedEmbeds = new ArrayList<MessageEmbed>();
		int remainFields = fieldCount;
		int activeEmbedIndex = 0;
		int channelIndex = 0;
		while (/*remainFields > 0 &&*/ activeEmbedIndex < numOfEmbeds) {
			
			ActiveEmbed ae = aes.get(activeEmbedIndex);
			
			//Try to retrieve message. If its unknown, handle the error.
			Message m = null;
			try {
				m = DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).retrieveMessageById(ae.getMessageId()).complete();
			} catch (ErrorResponseException e) {
				Logger.log(Level.FATAL, e.getErrorCode() + "");
				Logger.log(Level.FATAL, "Message still unknown after being replaced. Aborting Update. Inform Eclipse if thrown.");
				return;
			}
			
			EmbedBuilder eb = new EmbedBuilder(m.getEmbeds().get(0));
			eb.clearFields();
			
			int i = 0;
			while (i < 25 && i < remainFields) {
				String ch = liveChannels.get(channelIndex);
				Stream s = TwitchAPI.getTwitchStream(ch);
				
				HVStreamer hv = CommandUtils.getUserWithTwitchChannel(ch);
				String game = TwitchAPI.getGameName(s.getGameId());
				eb.addField(s.getUserName() + "[" + hv.getDiscordName() + "]", game+": ["+s.getTitle()+"]("+TwitchUtils.getTwitchChannelUrl(ch)+")", false);
				eb.setTitle("[" + (activeEmbedIndex+1) + "/" + numOfEmbeds + "] Live Hunsterverse Streamers (" + liveChannels.size() + " live)");
				
				i++;
				channelIndex++;
			}
			
			if (liveChannels.size() <= 0) {
				eb.setTitle("[" + (activeEmbedIndex+1) + "/" + numOfEmbeds + "] Live Hunsterverse Streamers (" + liveChannels.size() + " live)");
			}
			updatedEmbeds.add(eb.build());
			remainFields -= i;
			activeEmbedIndex++;
			Logger.log(Level.INFO, "Updating embed " + activeEmbedIndex);
		}
		
		
		activeEmbedIndex = 0;
		while (activeEmbedIndex < numOfEmbeds && updatedEmbeds.size() > 0) {
			ActiveEmbed ae = aes.get(activeEmbedIndex);
			System.out.println(aes.size() + ":" + updatedEmbeds.size());
			DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).editMessageById(ae.getMessageId(), updatedEmbeds.get(activeEmbedIndex)).complete();
			activeEmbedIndex++;
		}
		
		Logger.log(Level.SUCCESS, "Finished updating embeds...");
	}
	
	public static void sendTimedMessaged(CommandEvent event, String message, int ms, boolean isPrivate) {
		
		if (isPrivate) {
			event.getMember().getUser().openPrivateChannel().queue(channel -> {
				channel.sendMessage(message).queue( m -> {
					m.delete().queueAfter(ms, TimeUnit.MICROSECONDS);
				});
			});
			
			return;
		}
		
		event.getChannel().sendMessage(message).queue( m -> {
			m.delete().queueAfter(ms, TimeUnit.MILLISECONDS);
		});
	}
	
	public static void sendMessage(CommandEvent event, String message, boolean isPrivate) {
		
		if (isPrivate) {
			event.getMember().getUser().openPrivateChannel().queue(channel -> {
				channel.sendMessage(message).queue();
			});
			
			return;
		}
		
		event.getChannel().sendMessage(message).queue();
	}
	
	public static void sendMessage(String channelId, String message) {
		
		String guildId = DiscordBot.configuration.getGuildId();
		
		DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).sendMessage(message).queue();
	}
	
	public static void sendMessage(String channelId, MessageEmbed embed) {
		
		String guildId = DiscordBot.configuration.getGuildId();
		
		DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).sendMessage(embed).queue();
	}
	
	public static void sendRelayMessage(String message) {

		String guildId = DiscordBot.configuration.getGuildId();
		
		for (String id: DiscordBot.configuration.getFeatures().get("discord_message_relay").getChannels()) {
			DiscordBot.jda.getGuildById(guildId).getTextChannelById(id).sendTyping().queue();
			DiscordBot.jda.getGuildById(guildId).getTextChannelById(id).sendMessage(message).queue();
		}
	}
	
	public static void sendRelayMessage(MessageEmbed embed, InputStream file) {
		String guildId = DiscordBot.configuration.getGuildId();
		
		for (String id: DiscordBot.configuration.getFeatures().get("discord_message_relay").getChannels()) {
			DiscordBot.jda.getGuildById(guildId).getTextChannelById(id).sendTyping().queue();
			DiscordBot.jda.getGuildById(guildId).getTextChannelById(id).sendFile(file, "thumbnail.png").embed(embed).queue();
		}
	}
	
	public static void sendRelayMessage(MessageEmbed embed) {
		String guildId = DiscordBot.configuration.getGuildId();
		
		for (String id: DiscordBot.configuration.getFeatures().get("discord_message_relay").getChannels()) {
			DiscordBot.jda.getGuildById(guildId).getTextChannelById(id).sendTyping().queue();
			DiscordBot.jda.getGuildById(guildId).getTextChannelById(id).sendMessage(embed).queue();
			
		}
	}
	
	public static boolean giveRole(CommandEvent event, String discordId, String roleId) {
		Role role = event.getGuild().getRoleById(roleId);
		
		if (role == null) {
			return false;
		}
		
		event.getGuild().addRoleToMember(discordId, event.getGuild().getRoleById(roleId)).complete();
		return true;
	}
	
	public static boolean removeRole(CommandEvent event, String discordId, String roleId) {
		Role role = event.getGuild().getRoleById(roleId);
		
		if (role == null) {
			return false;
		}
		
		event.getGuild().removeRoleFromMember(discordId, role).complete();
		return true;
	}
	
	public static void setBotStatus(String status) {
		DiscordBot.jda.getPresence().setActivity(Activity.of(ActivityType.STREAMING, status));
	}

	

}
