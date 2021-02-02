package twitch.hunsterverse.net.discord;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class DiscordUtils {

	
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
