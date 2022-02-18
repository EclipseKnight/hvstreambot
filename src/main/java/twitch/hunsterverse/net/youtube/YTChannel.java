package twitch.hunsterverse.net.youtube;

public class YTChannel {

	private String channelId;
	private String channelName;
	private String streamTitle;
	private boolean isLive;
	
	public YTChannel(String channelId, String channelName, String streamTitle, boolean isLive) {
		this.channelId = channelId;
		this.channelName = channelName;
		this.streamTitle = streamTitle;
		this.isLive = isLive;
	}
	
	public YTChannel() {
		this.channelId = null;
		this.channelName = null;
		this.streamTitle = null;
		this.isLive = false;
	}

	public String getChannelId() {
		return channelId;
	}
	
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	
	public String getChannelName() {
		return channelName;
	}
	
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	
	public String getStreamTitle() {
		return streamTitle;
	}

	public void setStreamTitle(String streamTitle) {
		this.streamTitle = streamTitle;
	}

	public boolean isLive() {
		return isLive;
	}
	
	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}
	
	public String getChannelUrl() {
		return String.format("https://youtube.com/channel/%s/live", channelId);
	}
	
}
