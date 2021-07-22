package twitch.hunsterverse.net.discord.commands.subscription;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;

public class DiscordCommandStreamers extends Command {

	final String feature = "discord_command_streamers";
	public DiscordCommandStreamers() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases = DiscordBot.configuration.getFeatures().get(feature).getAliases();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		final int limit = 15;
		int index = 0;
		int page = 1;
		if (!event.getArgs().isBlank()) {
			page = Integer.valueOf(event.getArgs().trim());
			index = (15 * page) -15;
		}
	
		List<HVStreamer> streamers = JsonDB.database.getCollection(HVStreamer.class);
		Collections.sort(streamers, new Comparator<HVStreamer>() {

			@Override
			public int compare(HVStreamer o1, HVStreamer o2) {
				return o1.getTwitchChannel().compareTo(o2.getTwitchChannel());
			}
			
		});
		int numOfStreamers = streamers.size();
		int numOfPages = (int) Math.ceil((double) numOfStreamers/limit);
		
		if (page > numOfPages) {
			DiscordUtils.sendTimedMessage(event, 
					DiscordUtils.createShortEmbed("Error: page does not exist.", "Max number of pages: " + numOfPages, 
							DiscordBot.COLOR_FAILURE), 
					10000, false);
			return;
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Hunsterverse Streamers <a:livesmall:848591733658615858> ["+ page + "/" + numOfPages + "]");
		eb.setColor(DiscordBot.COLOR_STREAMER);
		if (page >= numOfPages) {
			eb.setFooter("No more pages...");
		} else {
			eb.setFooter("Use '" + DiscordBot.PREFIX + " streamers " + (page+1) + "' - to display the next page.");
		}
		
		int i = 0;
		while(i < limit && index < numOfStreamers ) {
			HVStreamer s = streamers.get(index);
			eb.appendDescription("`" + (index+1) + ".` " + s.getTwitchChannel() + "[**" + s.getDiscordName() + "**] (`" + s.getDiscordId() + "`) `" 
			+ CommandUtils.getTimedStreamedReadable(s.getTimeStreamed()) + "`\n");
			i++;
			index++;
		}
		
		DiscordUtils.sendMessage(event, eb.build(), false);
		
	}

}
