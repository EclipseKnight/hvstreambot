package twitch.hunsterverse.net.twitch;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchConfiguration {

    private Boolean debug;

    private Map<String, String> bot;

    private Map<String, String> api;

    private Map<String, String> credentials;
    
    private Map<String, TwitchFeature> features;

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public Map<String, String> getBot() {
        return bot;
    }

    public void setBot(Map<String, String> bot) {
        this.bot = bot;
    }

    public Map<String, String> getApi() {
        return api;
    }

    public void setApi(Map<String, String> api) {
        this.api = api;
    }

    public Map<String, String> getCredentials() {
        return credentials;
    }

    public void setCredentials(Map<String, String> credentials) {
        this.credentials = credentials;
    }

    public Map<String, TwitchFeature> getFeatures() {
		return features;
	}

    public void setFeatures(Map<String, TwitchFeature> features) {
		this.features = features;
	}
    
    @Override
	public String toString() {
		return String.format("""
				Configuration:
					bot = %s
					channels = %s,
					listener_channels = %s
					features:
						discord_command_is_live:
					 		enabled = %s,
					 		mod_only = %s
					 		name = %s,
					 		channels = %s
				""", bot,
				features.get("twitch_command_is_live").isEnabled(),
				features.get("twitch_command_is_live").isModOnly(),
				features.get("twitch_command_is_live").getName());
	}
}
