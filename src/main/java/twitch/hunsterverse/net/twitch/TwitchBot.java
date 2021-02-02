package twitch.hunsterverse.net.twitch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;

import twitch.hunsterverse.net.Launcher;
import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;
import twitch.hunsterverse.net.twitch.commands.ChannelCommandHandler;
import twitch.hunsterverse.net.twitch.features.ChannelOnGoLive;
import twitch.hunsterverse.net.twitch.features.ChannelOnGoOffline;

public class TwitchBot {

    /**
     * Holds the Bot Configuration
     */
    public static TwitchConfiguration configuration;

    /**
     * Twitch4J API
     */
    public static TwitchClient twitchClient;

    /**
     * Constructor
     */
    public TwitchBot() {
        // Load Configuration
        loadConfiguration();

        if (configuration.getApi().get("twitch_client_id") == null 
        		|| configuration.getApi().get("twitch_client_secret") == null 
        		|| configuration.getCredentials().get("irc") == null) {
        	Logger.log(Level.FATAL, "Twitch id, secret, or irc token is not set. Check the twitchbot.yaml keys: twitch_client_irc, twitch_client_id, twitch_client_id values.");
        	Logger.log(Level.FATAL, "Exiting...");
        	System.exit(1);
        }
        
        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        //region Auth
        OAuth2Credential credential = new OAuth2Credential(
                "twitch",
                configuration.getCredentials().get("irc")
        );
        //endregion

        //region TwitchClient
        twitchClient = clientBuilder
                .withClientId(configuration.getApi().get("twitch_client_id"))
                .withClientSecret(configuration.getApi().get("twitch_client_secret"))
                .withEnableHelix(true)
                /*
                 * Chat Module
                 * Joins irc and triggers all chat based events (viewer join/leave/sub/bits/gifted subs/...)
                 */
                .withChatAccount(credential)
                .withEnableChat(true)
                /*
                 * GraphQL has a limited support
                 * Don't expect a bunch of features enabling it
                 */
                .withEnableGraphQL(true)
                /*
                 * Kraken is going to be deprecated
                 * see : https://dev.twitch.tv/docs/v5/#which-api-version-can-you-use
                 * It is only here so you can call methods that are not (yet)
                 * implemented in Helix
                 */
                .withEnableKraken(true)
                /*
                 * Set default client id/secret pair for helix endpoints
                 */
                .withDefaultAuthToken(credential)
                /*
                 * Build the TwitchClient Instance
                 */
                .build();
        //endregion
    }

    /**
     * Method to register all features
     */
    @SuppressWarnings("unused")
	public void registerFeatures() {
		SimpleEventHandler eventHandler = twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class);

        // Register Event-based features
//		WriteChannelChatToConsole writeChannelChatToConsole = new WriteChannelChatToConsole(eventHandler);
		ChannelCommandHandler channelCommandHandler = new ChannelCommandHandler(eventHandler);
		ChannelOnGoLive channelOnGoLive = new ChannelOnGoLive(eventHandler);
		ChannelOnGoOffline channelOnGoOffline = new ChannelOnGoOffline(eventHandler);
		
		twitchClient.getClientHelper().enableStreamEventListener(TwitchUtils.getListenerChannels());
    }

    /**
     * Load the Configuration
     */
    public static void loadConfiguration() {
    	
    	
    	File twitchBotConfig = new File(Launcher.uwd + File.separator + "hvstreambot" + File.separator + "configs" + File.separator + "twitchbot.yaml");
    	
    	if (new File(Launcher.uwd + File.separator + "hvstreambot" + File.separator + "configs").mkdirs()) {
    		Logger.log(Level.WARN, "Generating configs directory...");
    	}
    	
    	try {
    		
    		if (!twitchBotConfig.exists()) {
        		generateConfig();
        	}
        	
            InputStream is = new BufferedInputStream(new FileInputStream(twitchBotConfig));
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            configuration = mapper.readValue(is, TwitchConfiguration.class);
            
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
        	Logger.log(Level.WARN, "Missing twitchbot.yaml. Generating new config...");
        	ClassLoader classloader = TwitchBot.class.getClassLoader();
        	
        	// copies twitchbot.yaml template to current working directory.
        	InputStream original = classloader.getResourceAsStream("twitchbot.yaml");
            Path copy = Paths.get(new File(Launcher.uwd + File.separator + "hvstreambot" + File.separator + "configs" + File.separator + "twitchbot.yaml").toURI());
          
            Logger.log(Level.WARN, "Generating config at " + copy);
            Files.copy(original, copy);
            
		} catch (IOException e) {
			e.printStackTrace();
			Logger.log(Level.ERROR, "Failed to generate twitchbot.yaml...");
		}
    }

    public void start() {
        // Connect to all channels
        for (String channel : TwitchUtils.getListenerChannels()) {
            twitchClient.getChat().joinChannel(channel);
//            twitchClient.getChat().sendMessage(channel, "Twitch bot is started.");
        }
    }

}
