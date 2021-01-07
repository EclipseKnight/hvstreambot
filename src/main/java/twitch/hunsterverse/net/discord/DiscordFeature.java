package twitch.hunsterverse.net.discord;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DiscordFeature {
	
	private boolean enabled;
	private String name;
	private List<String> channels;
	
	@JsonCreator
	public DiscordFeature(
			@JsonProperty("enabled") boolean enabled, 
			@JsonProperty("name") String name,
			@JsonProperty("channels") List<String> channels) {
		this.enabled = enabled;
		this.name = name;
		this.channels = channels;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<String> getChannels() {
		return channels;
	}
	
	public void setChannels(List<String> channels) {
		this.channels = channels;
	}
}
