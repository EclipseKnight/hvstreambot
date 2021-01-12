package twitch.hunsterverse.net.discord;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import twitch.hunsterverse.net.Launcher;
import twitch.hunsterverse.net.discord.commands.DiscordCommandConfiguration;
import twitch.hunsterverse.net.discord.commands.DiscordCommandIsLive;
import twitch.hunsterverse.net.discord.commands.DiscordCommandRestart;
import twitch.hunsterverse.net.discord.commands.DiscordCommandUpdate;
import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;

public class DiscordBot {

	/**
	 * Holds the Bot Configuration
	 */
	public static DiscordConfiguration configuration;
	
	/**
	 * JDA API
	 */
	public static JDA jda = null;
	
	/**
	 * Command builder jdautilities
	 */
	private CommandClientBuilder builder;
	
	/**
	 * intents of the discord bot
	 */
	private ArrayList<GatewayIntent> intents = new ArrayList<>();
	
	
	public DiscordBot() {
		// Load Configuration
		loadConfiguration();
		
		 if (configuration.getApi().get("discord_client_id") == null 
	        		|| configuration.getApi().get("discord_client_token") == null
	        		|| configuration.getOwnerId() == null) {
	        	Logger.log(Level.FATAL, "Discord id or token or owner id is not set. Check the discordbot.yaml keys: discord_client_id, discord_client_token, and owner_id values.");
	        	Logger.log(Level.FATAL, "Exiting...");
	        	System.exit(1);
	        }
		
		
		for (GatewayIntent gt : GatewayIntent.values()) {
			intents.add(0, gt);
		}
		
		
		try {
			jda = JDABuilder.createLight(configuration.getApi().get("discord_client_token"), intents).build().awaitReady();
		} catch (LoginException | InterruptedException e) {
			System.out.println("Discord bot failed to initialize: " + e.toString());
			return;
		}
	
		// create a command builder to register commands
		builder = new CommandClientBuilder();
		
		// Sets the owner id (commands can be set to owner/co-owner only)
		builder.setOwnerId(configuration.getOwnerId());
		
		// Sets the co owners. #setCoOwnerIds takes a variable argument so converting from List to string[] is necessary.
		builder.setCoOwnerIds(configuration.getCoOwnerIds().toArray(new String[configuration.getCoOwnerIds().size()]));
		
		// Sets the command prefix (e.g. !c isLive Name)
		builder.setPrefix("!c ");
		
		// Sets the default help command
		builder.setHelpWord("help");
		
		// Register the commands to the builder.
		registerCommands(builder);
		
		// For displaying currently live streamer as a status. Not currently working
		//TODO implement
		jda.getPresence().setActivity(Activity.of(ActivityType.DEFAULT, "Streamers \"Live\": 0 streamer(s)"));
		jda.getSelfUser().getManager().setName(configuration.getBot().get("name"));
	}
	
	
	
	private void registerCommands(CommandClientBuilder builder) {
		// adds command to builder
		
		builder.addCommand(new DiscordCommandConfiguration());
		builder.addCommand(new DiscordCommandRestart());
		builder.addCommand(new DiscordCommandUpdate());
		builder.addCommand(new DiscordCommandIsLive());
		
		// built command client
		CommandClient cmdClient = builder.build();
		
		// adds command client to listener. Commands use events will now be checked for on the discord server.
		jda.addEventListener(cmdClient);
	}
	
	 /**
     * Load the Configuration
     */
    public static void loadConfiguration() {
    	
    	
    	File twitchBotConfig = new File(Launcher.configPath + File.separator + "configs" + File.separator + "discordbot.yaml");
    	
    	if (new File(Launcher.configPath + File.separator + "configs").mkdir()) {
    		Logger.log(Level.WARN, "Generating configs directory...");
    	}
    	
    	try {
    		
    		if (!twitchBotConfig.exists()) {
        		generateConfig();
        	}
        	
            InputStream is = new BufferedInputStream(new FileInputStream(twitchBotConfig));
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            configuration = mapper.readValue(is, DiscordConfiguration.class);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.log(Level.FATAL, "Unable to load configuration... Exiting.");
            System.exit(1);
        }
    	
    }
    
    /**
     * Generates config file.
     */
    public static void generateConfig() {
    	
        try {
        	Logger.log(Level.WARN, "Missing discordbot.yaml. Generating new config...");
        	ClassLoader classloader = DiscordBot.class.getClassLoader();
        	
        	// copies twitchbot.yaml template to current working directory.
        	InputStream original = classloader.getResourceAsStream("discordbot.yaml");
            Path copy = Paths.get(new File(Launcher.configPath + File.separator + "configs" + File.separator + "discordbot.yaml").toURI());
          
            Logger.log(Level.WARN, "Generating config at " + copy);
            Files.copy(original, copy);
            
		} catch (IOException e) {
			e.printStackTrace();
			Logger.log(Level.ERROR, "Failed to generate discordbot.yaml...");
		}
    }
}
