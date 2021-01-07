package twitch.hunsterverse.net.twitch;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TwitchFeature {
	private boolean enabled;
	private boolean modOnly;
	private String name;
	private List<String> channels;
	
	@JsonCreator
	public TwitchFeature(
			@JsonProperty("enabled") boolean enabled, 
			@JsonProperty("mod_only") boolean modOnly,
			@JsonProperty("name") String name,
			@JsonProperty("channels") List<String> channels) {
		this.enabled = enabled;
		this.modOnly = modOnly;
		this.name = name;
		this.channels = channels;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isModOnly() {
		return modOnly;
	}
	
	public void setModOnly(boolean modOnly) {
		this.modOnly = modOnly;
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
