package twitch.hunsterverse.net.discord;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscordConfiguration {

	private Map<String, String> bot;

	private Map<String, String> api;
	
	private String ownerId;
	
	private List<String> coOwnerIds;

	private List<String> channels;

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
	
	public String getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	
	public List<String> getCoOwnerIds() {
		return coOwnerIds;
	}

	public List<String> getChannels() {
		return channels;
	}

	public void setChannels(List<String> channels) {
		this.channels = channels;
	}

	@Override
	public String toString() {
		return "Configuration{" + "bot=" + bot + ", api=" + api + ", channels=" + channels + '}';
	}
}
