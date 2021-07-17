package twitch.hunsterverse.net.discord.commands.gamefilters;

import java.util.HashMap;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamerConfig;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;

public class DiscordCommandGameFilterList extends Command {

	final String feature = "discord_command_game_filter";
	public DiscordCommandGameFilterList() {
		this.name = "list";
		this.aliases = new String[] {"ls"};
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, arguments)) {
			return;
		}
		
		//!s gamefilter list [filtername]
		HVStreamerConfig config = CommandUtils.getStreamerConfigWithDiscordId(event.getAuthor().getId());
		
		if (config == null) {
			config = new HVStreamerConfig();
			config.setDiscordId(event.getAuthor().getId());
			config.setSelectedFilter("hv_games");
			config.setGameFilters(new HashMap<String, List<String>>());
			config.setGameFilters(CommandUtils.addDefaultFilters(new HashMap<String, List<String>>()));
			JsonDB.database.upsert(config);
		}
		
		//!s gamefilter list
		if (event.getArgs().isBlank()) {
			EmbedBuilder eb = new EmbedBuilder();
			
			eb.setTitle("GameFilters:");
			eb.setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl());
			eb.setFooter("Key: * = currently selected filter.");
			eb.setColor(DiscordBot.COLOR_SUCCESS);
			for (String fName: config.getGameFilters().keySet()) {
				if (config.getSelectedFilter().equals(fName)) {
					fName += "*";
				}
				eb.appendDescription("-> " + fName + "\n");
			}
			
			DiscordUtils.sendMessage(event.getChannel().getId(), eb.build());
			return;
		}
		
		//!s gamefilter list filterName
		String filterName = event.getArgs().trim();
		
		if (!config.getGameFilters().containsKey(filterName)) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.appendDescription("Error: Custom filter \"" + filterName + "\" does not exist.");
			eb.setFooter("'" + DiscordBot.PREFIX + "filter list' - to display all of your filters.");
			eb.setColor(DiscordBot.COLOR_FAILURE);
			DiscordUtils.sendTimedMessage(event, eb.build(), 15000, false);
			return;
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(filterName);
		eb.setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl());
		eb.setFooter("Use '" + DiscordBot.PREFIX + "filter create " + filterName + " game1, game2, ...' to overwrite the filter.");
		eb.setColor(DiscordBot.COLOR_SUCCESS);
		eb.appendDescription("Games:\n");
		for (String game: config.getGameFilters().get(filterName)) {
			eb.appendDescription("-> " + game + "\n");
		}
		
		DiscordUtils.sendMessage(event.getChannel().getId(), eb.build());
	}

}
