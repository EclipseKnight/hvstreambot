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

public class DiscordCommandGameFilterDelete extends Command {

	final String feature = "discord_command_game_filter";
	public DiscordCommandGameFilterDelete() {
		this.name = "delete";
	}
	
	@Override
	protected void execute(CommandEvent event) {
		//!s gamefilter delete <name>
		
		if (event.getArgs().isBlank()) {
			DiscordUtils.sendTimedMessage(event, 
					DiscordUtils.createShortEmbed("Invalid Arguments.", 
							DiscordBot.PREFIX + "filter delete <filtername>",
							DiscordBot.COLOR_FAILURE), 15000, false);
			return;
		}
		
		String filterName = event.getArgs().trim();
		
		//Fetch config, if null, generate default config.
		HVStreamerConfig config = CommandUtils.getStreamerConfigWithDiscordId(event.getAuthor().getId());
		if (config == null) {
			config = new HVStreamerConfig();
			config.setDiscordId(event.getAuthor().getId());
			config.setSelectedFilter("hv_games");
			config.setGameFilters(CommandUtils.addDefaultFilters(new HashMap<String, List<String>>()));
		}
		
		//Check if default filter.
		if ("mh_games".equals(filterName) || "all_games".equals(filterName) || "hv_games".equals(filterName)) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.appendDescription("Error: You cannot delete the default filters.");
			eb.setFooter("Default filters: all_games, mh_games, hv_games");
			eb.setColor(DiscordBot.COLOR_FAILURE);
			DiscordUtils.sendTimedMessage(event, eb.build(), 10000, false);
			return;
		}
		
		//Check if filter exists.
		if (!config.getGameFilters().containsKey(filterName)) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.appendDescription("Error: Custom filter \"" + filterName + "\" does not exist.");
			eb.setFooter("Use '" + DiscordBot.PREFIX + "filter list' - to display all of your filters.");
			eb.setColor(DiscordBot.COLOR_FAILURE);
			DiscordUtils.sendTimedMessage(event, eb.build(), 15000, false);
			return;
		}
		
		//Remove filter from map. If selected, set to default filter.
		config.getGameFilters().remove(filterName);
		if (filterName.equals(config.getSelectedFilter())) {
			config.setSelectedFilter("hv_games");
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Filter \"" + filterName  + "\" deleted!");
		eb.setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl());
		eb.setFooter("Use `" + DiscordBot.PREFIX + "create` to create a new filter");
		eb.setColor(DiscordBot.COLOR_FAILURE);
		eb.appendDescription("Filters:\n");
		for (String fName: config.getGameFilters().keySet()) {
			eb.appendDescription("-> " + fName + "\n");
		}
		
		JsonDB.database.upsert(config);
		DiscordUtils.sendMessage(event.getChannel().getId(), eb.build());
	}

}
