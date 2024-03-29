package twitch.hunsterverse.net.database.documents;

import java.util.Map;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "streamers", schemaVersion = "1.0")
public class HVStreamer {

	
	@Id
	private String discordId;
	private String discordName;
	private String twitchChannel;
	private String youtubeChannelId;
	
	private boolean linked;
	private boolean affiliate;
	private boolean pingable;
	private boolean streaming;
	private boolean isLiveTwitch;
	private boolean isLiveYoutube;
	private long timeStreamed;
	
	// uuid:user discord id
	private Map<String, String> subscribers;
	
	
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
	
	public String getTwitchChannel() {
		return twitchChannel;
	}
	
	public void setTwitchChannel(String twitchChannel) {
		this.twitchChannel = twitchChannel;
	}
	
	public String getYoutubeChannelId() {
		return youtubeChannelId;
	}
	
	public void setYoutubeChannelId(String youtubeChannelId) {
		this.youtubeChannelId = youtubeChannelId;
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
	
	public boolean isPingable() {
		return pingable;
	}
	
	public void setPingable(boolean pingable) {
		this.pingable = pingable;
	}
	
	public boolean isStreaming() {
		return streaming;
	}
	
	public void setStreaming(boolean streaming) {
		this.streaming = streaming;
	}
	
	public boolean isLiveTwitch() {
		return isLiveTwitch;
	}

	public void setLiveTwitch(boolean isLiveTwitch) {
		this.isLiveTwitch = isLiveTwitch;
	}

	public boolean isLiveYoutube() {
		return isLiveYoutube;
	}

	public void setLiveYoutube(boolean isLiveYoutube) {
		this.isLiveYoutube = isLiveYoutube;
	}

	public Map<String, String> getSubscribers() {
		return subscribers;
	}
	
	public void setSubscribers(Map<String, String> subscribers) {
		this.subscribers = subscribers;
	}
	
	public long getTimeStreamed() {
		return timeStreamed;
	}
	
	public void setTimeStreamed(long timeStreamed) {
		this.timeStreamed = timeStreamed;
	}
}
