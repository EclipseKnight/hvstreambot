package twitch.hunsterverse.net.discord.commands.gamefilters;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.discord.commands.CommandUtils;

public class DiscordCommandGameFilter extends Command {

	final String feature = "discord_command_game_filter";
	public DiscordCommandGameFilter() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases = DiscordBot.configuration.getFeatures().get(feature).getAliases();
		this.children = new Command[] {new DiscordCommandGameFilterCreate(),
				new DiscordCommandGameFilterDelete(),
				new DiscordCommandGameFilterList(),
				new DiscordCommandGameFilterSelect()};
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtils.fullUsageCheck(event, arguments)) {
			return;
		}
		
		if (event.getArgs().isBlank()) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(DiscordBot.COLOR_FAILURE);
			eb.appendDescription("Invalid Arguments");
			eb.setFooter(DiscordBot.PREFIX + "filter [create, delete, list, select]");
			DiscordUtils.sendTimedMessage(event, eb.build(), 30000, false);
		}
	}
}
