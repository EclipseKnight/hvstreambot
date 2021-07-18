package twitch.hunsterverse.net.discord.commands.owner.config;

import java.util.HashMap;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.database.documents.HVStreamerConfig;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;

public class DiscordCommandConfigurationReload extends Command {

	final String feature = "discord_command_config";
	public DiscordCommandConfigurationReload() {
		this.name = "reload";
	}

	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		DiscordBot.loadConfiguration();
		
		DiscordUtils.sendMessage(event, 
				DiscordUtils.createShortEmbed("Configuration reloaded!", 
						null,
						DiscordBot.COLOR_SUCCESS), false);
		
		for (HVStreamer s: JsonDB.database.getCollection(HVStreamer.class)) {
			HVStreamerConfig config = CommandUtils.getStreamerConfigWithDiscordId(s.getDiscordId());
			if (config == null) {
				config = new HVStreamerConfig();
				config.setDiscordId(event.getAuthor().getId());
				config.setSelectedFilter("hv_games");
				config.setGameFilters(new HashMap<String, List<String>>());
				config.setGameFilters(CommandUtils.addDefaultFilters(new HashMap<String, List<String>>()));
				JsonDB.database.upsert(config);
				continue;
			}
			config.setSelectedFilter("hv_games");
			JsonDB.database.upsert(config);
		}
		event.reply("all users updated to hv_games.");
	}
}
