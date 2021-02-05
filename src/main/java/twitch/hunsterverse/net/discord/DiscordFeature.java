package twitch.hunsterverse.net.discord;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DiscordFeature {
	
	private boolean enabled;
	private boolean linked;
	private boolean affiliate;
	private String name;
	private String[] aliases;
	private String description;
	private List<String> channels;
	private List<String> roles;
	
	@JsonCreator
	public DiscordFeature(
			@JsonProperty("enabled") boolean enabled,
			@JsonProperty("linked") boolean linked,
			@JsonProperty("affiliate") boolean affiliate,
			@JsonProperty("name") String name,
			@JsonProperty("asliases") String[] aliases,
			@JsonProperty("description") String description,
			@JsonProperty("channels") List<String> channels,
			@JsonProperty("roles") List<String> roles) {
		this.enabled = enabled;
		this.linked = linked;
		this.affiliate = affiliate;
		this.name = name;
		this.aliases = aliases;
		this.description = description;
		this.channels = channels;
		this.roles = roles;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isLinked() {
		return linked;
	}
	
	public void setLinked(boolean linked) {
		this.linked = linked;
	}
	
	public boolean isAffiliate() {
		return affiliate;
	}
	
	public void setAffiliate(boolean affiliate) {
		this.affiliate = affiliate;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String[] getAliases() {
		return aliases;
	}
	
	public void setAliases(String[] aliases) {
		this.aliases = aliases;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<String> getChannels() {
		return channels;
	}
	
	public void setChannels(List<String> channels) {
		this.channels = channels;
	}
	
	public List<String> getRoles() {
		return roles;
	}
	
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
