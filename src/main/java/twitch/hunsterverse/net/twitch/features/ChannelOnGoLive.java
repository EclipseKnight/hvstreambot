package twitch.hunsterverse.net.twitch.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.events.ChannelGoLiveEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import twitch.hunsterverse.net.database.HVStreamer;
import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;
import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;
import twitch.hunsterverse.net.twitch.TwitchUtils;

public class ChannelOnGoLive {

	String feature = "discord_message_relay";
	
	public ChannelOnGoLive(SimpleEventHandler eventHandler) {
		eventHandler.onEvent(ChannelGoLiveEvent.class, event -> onGoLive(event));
	}

	public void onGoLive(ChannelGoLiveEvent event) {
		if (TwitchAPI.recentlyOffline.getIfPresent(event.getChannel().getId()) == true) return;
		
		Logger.log(Level.INFO, event.getStream().getUserName() + " is now live.");
		HVStreamer s = CommandUtils.getUserWithTwitchChannel(event.getChannel().getName());
		s.setStreaming(true);
		JsonDB.database.upsert(s);
		DiscordUtils.setBotStatus((TwitchUtils.getLiveChannels().size()+1) + " streamer(s)");
		
		
		EmbedBuilder eb = new EmbedBuilder();
		
		eb.setAuthor(s.getDiscordName() + "[Twitch:" + s.getTwitchChannel() + "]",
				TwitchUtils.getTwitchChannelUrl(s.getTwitchChannel()),
				TwitchAPI.getTwitchUser(s.getTwitchChannel()).getProfileImageUrl());
		
		eb.setColor(DiscordBot.jda.getGuildById(DiscordBot.configuration.getGuildId()).getRoleById(DiscordBot.configuration.getStreamRoleId()).getColorRaw());	
		eb.setTitle(event.getStream().getTitle(), TwitchUtils.getTwitchChannelUrl(s.getTwitchChannel()));
		eb.addField("Game", TwitchAPI.getGameName(event.getStream().getGameId()), true);
		eb.addField("Viewers", event.getStream().getViewerCount().toString(), true);
		eb.setImage(event.getStream().getThumbnailUrl(440, 228));
		
		DiscordUtils.sendRelayMessage(eb.build());
	}
	
	
}
