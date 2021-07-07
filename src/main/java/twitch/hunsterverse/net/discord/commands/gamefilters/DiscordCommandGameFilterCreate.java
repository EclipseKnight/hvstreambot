package twitch.hunsterverse.net.discord.commands.gamefilters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.github.twitch4j.helix.domain.Game;
import com.github.twitch4j.helix.domain.GameList;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamerConfig;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;
import twitch.hunsterverse.net.twitch.TwitchBot;

public class DiscordCommandGameFilterCreate extends Command {

	final String feature = "discord_command_game_filter";
	public DiscordCommandGameFilterCreate() {
		this.name = "create";
	}
	
	@Override
	protected void execute(CommandEvent event) {
		//!s gamefilter create <name> <game1 game2 game3 ...>
		
		String[] args = CommandUtils.splitArgs(event.getArgs());
		
		if (args.length < 2) {
			DiscordUtils.sendTimedMessage(event, 
					DiscordUtils.createShortEmbed("Invalid Arguments.", 
							DiscordBot.PREFIX + "filter create <filtername> <game1, game2, game3, ...>",
							DiscordBot.COLOR_FAILURE), 30000, false);
			
			return;
		}
		
		String filterName = args[0];
		//quick thrown together line to split the comma separated games and remove spaces. 
		String[] inputList = event.getArgs().replace(filterName, "").substring(1).split(",[ ]*");
		
		GameList games = TwitchBot.twitchClient.getHelix().getGames(null, null, Arrays.asList(inputList)).execute();
		
		HVStreamerConfig config = CommandUtils.getStreamerConfigWithDiscordId(event.getAuthor().getId());
		if (config == null) {
			config = new HVStreamerConfig();
			config.setDiscordId(event.getAuthor().getId());
			config.setSelectedFilter("hv_games");
			config.setGameFilters(new HashMap<String, List<String>>());
			config.setGameFilters(CommandUtils.addDefaultFilters(new HashMap<String, List<String>>()));
		}
		
		if (config.getGameFilters().size() >= 25) {
			DiscordUtils.sendTimedMessage(event, 
					DiscordUtils.createShortEmbed("Error: You cannot create more than 25 filters.", 
							"'" + DiscordBot.PREFIX + "list' - to display all of your filters.",
							DiscordBot.COLOR_FAILURE), 15000, false);
			
			return;
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("New filter created!");
		eb.setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl());
		eb.setFooter("`" + DiscordBot.PREFIX +"delete " + filterName + "` to delete the filter.");
		eb.setColor(DiscordBot.COLOR_SUCCESS);
		eb.appendDescription("Filter: " + filterName + "\n");
		
		List<String> gameNames = new ArrayList<>();
		for(Game g: games.getGames()) {
			gameNames.add(g.getName());
			eb.appendDescription("-> " + g.getName() + "\n");
		}
		
		config.getGameFilters().put(filterName, gameNames);
		JsonDB.database.upsert(config);
		
		DiscordUtils.sendMessage(event.getChannel().getId(), eb.build());
	}

}
