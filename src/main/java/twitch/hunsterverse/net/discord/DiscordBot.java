package twitch.hunsterverse.net.discord;

import java.io.InputStream;
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
import twitch.hunsterverse.net.discord.commands.DiscordCommandIsLive;

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
		
		jda.getPresence().setActivity(Activity.of(ActivityType.DEFAULT, "Streamers \"Live\": 0 streamer(s)"));
	}
	
	
	
	private void registerCommands(CommandClientBuilder builder) {
		// adds command to builder
		builder.addCommand(new DiscordCommandIsLive());
		
		// built command client
		CommandClient cmdClient = builder.build();
		
		// adds command client to listener. Commands use events will now be checked for on the discord server.
		jda.addEventListener(cmdClient);
	}
	
	/**
     * Load the Configuration
     */
    private void loadConfiguration() {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("discordbot.yaml");

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            configuration = mapper.readValue(is, DiscordConfiguration.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Unable to load Configuration ... Exiting.");
            System.exit(1);
        }
    }
}
