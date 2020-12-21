package twitch.hunsterverse.net.twitch.chatbot;

import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;

import twitch.hunsterverse.net.twitch.chatbot.features.ChannelCommandHandler;
import twitch.hunsterverse.net.twitch.chatbot.features.WriteChannelChatToConsole;

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
		WriteChannelChatToConsole writeChannelChatToConsole = new WriteChannelChatToConsole(eventHandler);
		ChannelCommandHandler channelCommandHandler = new ChannelCommandHandler(eventHandler);
    }

    /**
     * Load the Configuration
     */
    private void loadConfiguration() {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("twitchbot.yaml");

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            configuration = mapper.readValue(is, TwitchConfiguration.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Unable to load Configuration ... Exiting.");
            System.exit(1);
        }
    }

    public void start() {
        // Connect to all channels
        for (String channel : configuration.getChannels()) {
            twitchClient.getChat().joinChannel(channel);
        }
    }

}
