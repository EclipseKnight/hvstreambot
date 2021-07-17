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

public class DiscordCommandGameFilterSelect extends Command {

	final String feature = "discord_command_game_filter";
	public DiscordCommandGameFilterSelect() {
		this.name = "select";
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		if (event.getArgs().isBlank()) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.appendDescription("Invalid Arguments");
			eb.setFooter(DiscordBot.PREFIX + "filter select <filtername>");
			eb.setColor(DiscordBot.COLOR_FAILURE);
			DiscordUtils.sendTimedMessage(event, eb.build(), 10000, false);
			return;
		}
		
		String filterName = event.getArgs().trim();
		
		HVStreamerConfig config = CommandUtils.getStreamerConfigWithDiscordId(event.getAuthor().getId());
		if (config == null) {
			config = new HVStreamerConfig();
			config.setDiscordId(event.getAuthor().getId());
			config.setSelectedFilter("hv_games");
			config.setGameFilters(new HashMap<String, List<String>>());
			config.setGameFilters(CommandUtils.addDefaultFilters(new HashMap<String, List<String>>()));
			JsonDB.database.upsert(config);
		}
		
		if (!config.getGameFilters().containsKey(filterName)) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.appendDescription("Error: filter \"" + filterName + "\" does not exist.");
			eb.setFooter("'" + DiscordBot.PREFIX + "filter list' - to display all of your filters.");
			eb.setColor(DiscordBot.COLOR_FAILURE);
			DiscordUtils.sendTimedMessage(event, eb.build(), 15000, false);
			return;
		}
		
		config.setSelectedFilter(filterName);
		JsonDB.database.upsert(config);
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl());
		eb.setTitle("Filter selected!");
		eb.appendDescription("Selected: " + filterName);
		eb.setFooter("Use '" + DiscordBot.PREFIX + "filter list' - to display all of your filters and active filter.");
		eb.setColor(DiscordBot.COLOR_SUCCESS);
		DiscordUtils.sendMessage(event.getChannel().getId(), eb.build());
	}

}
