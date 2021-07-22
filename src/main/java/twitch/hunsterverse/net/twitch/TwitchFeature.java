package twitch.hunsterverse.net.twitch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TwitchFeature {
	private boolean enabled;
	private boolean linked;
	private boolean affiliate;
	private boolean modOnly;
	private String name;
	private String[] aliases;
	private String description;
	
	@JsonCreator
	public TwitchFeature(
			@JsonProperty("enabled") boolean enabled, 
			@JsonProperty("linked") boolean linked,
			@JsonProperty("affiliate") boolean affiliate,
			@JsonProperty("mod_only") boolean modOnly,
			@JsonProperty("name") String name,
			@JsonProperty("asliases") String[] aliases,
			@JsonProperty("description") String description) {
		this.enabled = enabled;
		this.linked = linked;
		this.affiliate = affiliate;
		this.modOnly = modOnly;
		this.name = name;
		this.aliases = aliases;
		this.description = description;
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
}
