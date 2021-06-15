package twitch.hunsterverse.net.database.documents;

import java.util.Map;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "users", schemaVersion = "1.0")
public class HVUser {

	@Id
	private String discordId;
	private String discordName;
	
	private boolean notifsMuted;
	
	// streamerDiscordId:uuid
	private Map<String, String> subscriptions;
	
	
	
	public String getDiscordId() {
		return discordId;
	}
	
	public void setDiscordId(String discordId) {
		this.discordId = discordId;
	}
	
	public String getDiscordName() {
		return discordName;
	}
	
	public void setDiscordName(String discordName) {
		this.discordName = discordName;
	}
	
	public boolean isNotifsMuted() {
		return notifsMuted;
	}
	
	public void setNotifsMuted(boolean notifsMuted) {
		this.notifsMuted = notifsMuted;
	}
	
	public Map<String, String> getSubscriptions() {
		return subscriptions;
	}
	
	public void setSubscriptions(Map<String, String> subscriptions) {
		this.subscriptions = subscriptions;
	}
	
}
