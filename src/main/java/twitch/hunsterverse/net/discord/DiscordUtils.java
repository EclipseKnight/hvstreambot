package twitch.hunsterverse.net.discord;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.twitch4j.helix.domain.Stream;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.ActiveEmbed;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.database.documents.HVUser;
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
		
		EmbedBuilder eb = new EmbedBuilder()
				.setColor(DiscordBot.jda.getGuildById(DiscordBot.configuration.getGuildId()).getRoleById(DiscordBot.configuration.getStreamRoleId()).getColorRaw())
				.setTitle("Live Hunsterverse Streamers")
				.setFooter("Bot created by Eclipse. " + DiscordBot.VERSION);
		
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
	
	//cached recent list for updateLiveEmbeds
	private static List<String> recentLiveChannels = null;
	
	/**
	 * Update the active embeds.
	 * @param force true if you want to bypass any exits and force update the embeds.
	 */
	public static void updateLiveEmbeds(boolean force) {
		
		Logger.log(Level.SUCCESS, "[1/8] Initializing Embed Update...");
		long start = System.currentTimeMillis();
		long result = -1;
		String guildId = DiscordBot.configuration.getGuildId();
		String channelId = DiscordBot.configuration.getLiveEmbedChannel();
		
		List<ActiveEmbed> aes = JsonDB.database.getCollection(ActiveEmbed.class);
		List<String> liveChannels = TwitchUtils.getLiveFilteredChannels();
		
		boolean newLiveStreamer = false;
		
		Logger.log(Level.DEBUG, "[2/8] Checking if channels are live.");
		// No live channels, skip update or continue if forced.
		if (liveChannels.size() <= 0) {
			Logger.log(Level.INFO, "No live channels.");
			
			if (!force) {
				result = System.currentTimeMillis() - start;
				Logger.log(Level.WARN, "Skipping update. Time taken (MS): " + result);
				
				return;
			}
				
			Logger.log(Level.WARN, "Flag set to force. Forcing update...");
		}
		
		Logger.log(Level.DEBUG, "[3/8] Checking if new streamer is live...");
		//Check whether to highlight channel if new streamer is live. 
		//if there is a cached list.
		if (recentLiveChannels != null) {
			Collections.sort(recentLiveChannels);
			Collections.sort(liveChannels);
			
			//if lists aren't matching...
			if (!recentLiveChannels.equals(liveChannels)) {
				
				//check if old list is smaller than new list, indicating a new streamer is live.
				if (recentLiveChannels.size() < liveChannels.size()) {
					newLiveStreamer = true;
				}
				
				recentLiveChannels.clear();
				recentLiveChannels.addAll(liveChannels);
				Logger.log(Level.WARN, "New streamer is live!" + result);
				
			//If lists are matching, then no change in live channels.
			} else if (!force) {
				result = System.currentTimeMillis() - start;
				Logger.log(Level.WARN, "No change in live channels. Skipping update. Time taken (MS): " + result);
				
				return;
			//If flag set to force, then update the embed regardless.	
			} else {
				Logger.log(Level.WARN, "No change in live channels. Flag set to force. Continuing update...");
			}
			
		} else {
			recentLiveChannels = new ArrayList<String>();
			recentLiveChannels.addAll(liveChannels);
			newLiveStreamer = true;
		}
		
		
		Logger.log(Level.DEBUG, "[4/8] Adjusting embeds to meet needed quantity...");
		//Code to update the embeds begins here.
		int fieldCount = liveChannels.size();
		int numOfEmbeds = (int) Math.ceil(fieldCount / 25.0);
		
		if (numOfEmbeds <= 0) numOfEmbeds++;
		
		Logger.log(Level.WARN, numOfEmbeds + " active embeds are needed.");
		
		//adjust active embeds to meet required amount.
		if (numOfEmbeds > aes.size() || numOfEmbeds < aes.size()) {
			
			//If too few, increase until == to the needed amount
			if (numOfEmbeds > aes.size()) {
				Logger.log(Level.INFO, "Too few active embeds ("+aes.size()+"), creating more...");
				
				while (aes.size() < numOfEmbeds) {
					createLiveEmbed();
					aes = JsonDB.database.getCollection(ActiveEmbed.class);
					Logger.log(Level.SUCCESS, "Increased to: " + aes.size());
				}
			}
			
			//If too many, reduce until == to the needed amount
			if (numOfEmbeds < aes.size()) {
				Logger.log(Level.INFO, "Too many active embeds ("+aes.size()+"), reducing amount...");

				while (aes.size() > numOfEmbeds) {
					DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).deleteMessageById(aes.get(aes.size()-1).getMessageId()).queue();
					JsonDB.database.remove(aes.get(aes.size()-1), ActiveEmbed.class);
					aes = JsonDB.database.getCollection(ActiveEmbed.class);
					Logger.log(Level.SUCCESS, "Reduced to: " + aes.size());
				}
			}
		}
		Logger.log(Level.SUCCESS, "Adjusted current active embeds to meet the needed quantity (needed:storedindatabse) ("+numOfEmbeds +":"+aes.size() +").");
		
		
		Logger.log(Level.DEBUG, "[5/8] Checking if the embed messages still exist...");
		//Check each active embed to see if the message still exists. 
		for (ActiveEmbed ae: aes) {
			Message m = null;
			
			//Try to retrieve message. If its unknown, handle the error.
			try {
				m = DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).retrieveMessageById(ae.getMessageId()).complete();
			} catch (ErrorResponseException e) {
				Logger.log(Level.ERROR, "Discord error: " + e.getErrorCode());
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
		
		
		Logger.log(Level.DEBUG, "[6/8] Updating the embeds...");
		//Update loop
		List<MessageEmbed> updatedEmbeds = new ArrayList<MessageEmbed>();
		int remainFields = fieldCount;
		int activeEmbedIndex = 0;
		int channelIndex = 0;
		//loop through embeds until populated with needed fields/streamers
		while (activeEmbedIndex < numOfEmbeds) {
			Logger.log(Level.INFO, "Updating embed " + activeEmbedIndex);
			
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
			
			//Loop through adding the needed fields up until 25 field embed limit or needed fields are reahed. C
			int i = 0;
			while (i < 25 && i < remainFields) {
				String ch = liveChannels.get(channelIndex);
				Stream s = TwitchAPI.getTwitchStream(ch);
				
				HVStreamer hv = CommandUtils.getStreamerWithTwitchChannel(ch);
				String game = TwitchAPI.getGameName(s.getGameId());
				eb.addField("<a:livesmall:848591733658615858> " + s.getUserName() + "[" + hv.getDiscordName() + "]", " <:arrowquest:804000542678056980> :video_game: " +game+": ["+s.getTitle()+"]("+TwitchUtils.getTwitchChannelUrl(ch)+")", false);
				eb.setTitle("[" + (activeEmbedIndex+1) + "/" + numOfEmbeds + "] Live Hunsterverse Streamers (" + liveChannels.size() + " live)");
				
				i++;
				channelIndex++;
			}
			
			if (liveChannels.size() <= 0) {
				eb.setTitle("[" + (activeEmbedIndex+1) + "/" + numOfEmbeds + "] Live Hunsterverse Streamers (" + liveChannels.size() + " live)");
			}
			eb.setFooter("Bot created by Eclipse. " + DiscordBot.VERSION);
			
			
			updatedEmbeds.add(eb.build());
			remainFields -= i;
			activeEmbedIndex++;
		}
		
		//loop through current embed messages and edit/replace them with newly embed instances.
		activeEmbedIndex = 0;
		while (activeEmbedIndex < numOfEmbeds && updatedEmbeds.size() > 0) {
			ActiveEmbed ae = aes.get(activeEmbedIndex);
			DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).editMessageById(ae.getMessageId(), updatedEmbeds.get(activeEmbedIndex)).complete();
			activeEmbedIndex++;
		}
		
		//If a new streamer went live then highlight channel.
		if (newLiveStreamer) {
			Logger.log(Level.DEBUG, "[7/8] New streamer went live. Highlighting the channel...");
			DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).sendMessage("<:pepegaslam:595804056941887489>").queue((m) -> {
				m.delete().queue();
			});
		}
		
		result = System.currentTimeMillis() - start;
		Logger.log(Level.SUCCESS, "[8/8] Finished updating embeds... Time taken (MS): " + result + "\n");
		DiscordUtils.setBotStatus(liveChannels.size() + " streamer(s)");
	}
	
	/**
	 * Notifies all of the users in the streamers subscriber list unless user has muted notifications.
	 * @param streamerId
	 */
	public static void notifySubscribers(String streamerId, Stream stream) {
		long start = System.currentTimeMillis();
		long result = -1;
		
		Logger.log(Level.INFO, "Notifying subs...");
		
		
		HVStreamer s = CommandUtils.getStreamerWithDiscordId(streamerId);
		
		//Check if streamer exists.
		if (s == null) {
			Logger.log(Level.ERROR, streamerId + " (streamer) does not exist.");
			return;
		}
		
		//Loop through subscribers.
		s.getSubscribers().forEach((key, value) -> {
			//makeshift loop breaker.
			boolean cont = true;
			
			HVUser u = CommandUtils.getUserWithDiscordId(value);
			
			//Check if user exists
			if (u == null) {
				Logger.log(Level.ERROR, value + " (user) does not exist.");
				cont = false;
			}
			
			//if user has notifs muted
			if (u.isNotifsMuted()) {
				cont = false;
			}
			
			//If user notifs unmuted and exists.
			if (cont) {
				
				try {
					//fetch the member from the server. 
					DiscordBot.jda.getGuildById(DiscordBot.configuration.getGuildId()).retrieveMemberById(value).queue(m -> {
						//attempt to open a private channel and send the message.
						m.getUser().openPrivateChannel().queue(channel -> {
							EmbedBuilder eb = new EmbedBuilder();
							String game = TwitchAPI.getGameName(stream.getGameId());
							
							
							eb.setTitle(s.getDiscordName() + " is now live!");
							eb.addField("<a:livesmall:848591733658615858> " 
									+ stream.getUserName() + "[" + s.getDiscordName() + "]", 
									" <:arrowquest:804000542678056980> :video_game: " +game+": ["+stream.getTitle()+"]("+TwitchUtils.getTwitchChannelUrl(s.getTwitchChannel())+")", false);
							
							eb.setFooter("Use '!s togglenotifs' - to mute/un-mute all notifications.");
							eb.setTimestamp(Instant.now());
							eb.setColor(DiscordBot.COLOR_STREAMER);
							channel.sendMessage(eb.build()).queue();
						});
					});
					
				} catch (ErrorResponseException e) {
					Logger.log(Level.ERROR, "Discord error: " + e.getErrorCode());
					Logger.log(Level.WARN, "id:" + u.getDiscordId() + " -> Uknown User or Member: not in guild or user does not exist.\nRemoving mappings...");
					
					s.getSubscribers().remove(u.getSubscriptions().get(s.getDiscordId()));
					u.getSubscriptions().remove(s.getDiscordId());
					
					JsonDB.database.upsert(s);
					JsonDB.database.upsert(u);
					return;
				}
			}
		});
		
		result = System.currentTimeMillis() - start;
		Logger.log(Level.SUCCESS, "Finished notifying subs... Time taken (MS): " + result);
	}
	
	
	/**
	 * 
	 * @param author
	 * @param title
	 * @param description
	 * @param fields
	 * @param footer
	 * @param color
	 * @param timestamp
	 * @return
	 */
	public static MessageEmbed createEmbed(User author, String title, String description, List<Field> fields, String footer, int color, boolean timestamp) {
		EmbedBuilder eb = new EmbedBuilder();
		
		if (author != null)
			eb.setAuthor(author.getAsTag(), null, author.getAvatarUrl());
		
		eb.setTitle(title);
		eb.setDescription(description);
		
		if (fields != null) {
			for (Field f: fields) {
				eb.addField(f);
			}
		}
		
		eb.setFooter(footer);
		eb.setColor(color);
		
		if (timestamp) 
			eb.setTimestamp(Instant.now());
			
		return eb.build();
		
	}
	
	/**
	 * 
	 * @param description
	 * @param footer
	 * @param color
	 * @return
	 */
	public static MessageEmbed createShortEmbed(String description, String footer, int color) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setDescription(description);
		eb.setFooter(footer);
		eb.setColor(color);
		return eb.build();
	}
	
	/**
	 * 
	 * @param description
	 * @param footer
	 * @param color
	 * @return
	 */
	public static MessageEmbed createShortEmbed(String description, String footer, int color, boolean timestamp) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setDescription(description);
		eb.setFooter(footer);
		eb.setColor(color);
		if (timestamp) {
			eb.setTimestamp(Instant.now());
		}
		
		return eb.build();
	}
	
	public static void sendTimedMessage(CommandEvent event, String message, int ms, boolean isPrivate) {
		
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
	
	public static void sendTimedMessage(CommandEvent event, MessageEmbed embed, int ms, boolean isPrivate) {
		
		if (isPrivate) {
			event.getMember().getUser().openPrivateChannel().queue(channel -> {
				channel.sendMessage(embed).queue( m -> {
					m.delete().queueAfter(ms, TimeUnit.MICROSECONDS);
				});
			});
			
			return;
		}
		
		event.getChannel().sendMessage(embed).queue( m -> {
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
	
	public static void sendMessage(CommandEvent event, MessageEmbed embed, boolean isPrivate) {
		
		if (isPrivate) {
			event.getMember().getUser().openPrivateChannel().queue(channel -> {
				channel.sendMessage(embed).queue();
			});
			
			return;
		}
		
		event.getChannel().sendMessage(embed).queue();
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
		DiscordBot.jda.getPresence().setStatus(OnlineStatus.IDLE);
	}

	

}
