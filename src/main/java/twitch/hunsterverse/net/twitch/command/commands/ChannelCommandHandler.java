package twitch.hunsterverse.net.twitch.command.commands;

import java.util.HashMap;
import java.util.Map;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;
import twitch.hunsterverse.net.twitch.TwitchBot;
import twitch.hunsterverse.net.twitch.command.TwitchCommand;
import twitch.hunsterverse.net.twitch.command.TwitchCommandEvent;

public class ChannelCommandHandler {

	public Map<String, TwitchCommand> commands = new HashMap<>();
	
	public ChannelCommandHandler(SimpleEventHandler eventHandler) {
		eventHandler.onEvent(ChannelMessageEvent.class, event -> onChannelMessage(event));
	}
	
	/**
	 * Adds the provided TwitchCommands to the handler.
	 * @param cmds
	 */
	public void addCommands(TwitchCommand... cmds) {
		for (TwitchCommand cmd: cmds) {
			commands.put(cmd.getName(), cmd);
		}
	}

	public void onChannelMessage(ChannelMessageEvent event) {
		String msg = event.getMessage();
		
		// check if message is a command attempt. 
		if(event.getMessage().toLowerCase().startsWith(TwitchBot.PREFIX)) {

			// Get the arguments. 
			String argsFull = msg.substring(msg.indexOf(TwitchBot.PREFIX) + TwitchBot.PREFIX.length());
			String[] args = msg.substring(msg.indexOf(TwitchBot.PREFIX) + TwitchBot.PREFIX.length()).split("\\s+");
			
			//Check if map contains command attempted.
			if (commands.containsKey(args[0])) {
				TwitchCommand command = commands.get(args[0].toLowerCase());
				
				argsFull = argsFull.replace(args[0], "").stripLeading();
				
				switch (command.getFeature()) {
					case "twitch_command_is_live" -> {
						TwitchCommandIsLive cmd = (TwitchCommandIsLive) command;
						cmd.execute(new TwitchCommandEvent(event, argsFull));
					}
					
					case "twitch_command_restart" -> {
						TwitchCommandRestart cmd = (TwitchCommandRestart) command;
						cmd.execute(new TwitchCommandEvent(event, argsFull));
					}
					
					case "twitch_command_configuration" -> {
						TwitchCommandConfiguration cmd = (TwitchCommandConfiguration) command;
						cmd.execute(new TwitchCommandEvent(event, argsFull));
					}
					
					case "twitch_command_free_gh" -> {
						TwitchCommandFreeGH cmd = (TwitchCommandFreeGH) command;
						cmd.execute(new TwitchCommandEvent(event, argsFull));
					}
					
					default -> Logger.log(Level.ERROR, "Unexpected value: " + command.getFeature());
					
				}
			}
			
			
		}
	}
}
