package twitch.hunsterverse.net.youtube;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class YoutubeAPI {
	
	
	/**
	 * 
	 * @param channelId
	 * @return Map<String, String> containing the keys [status, channelId, channelName, isLive]
	 */
	public static YTChannel getChannel(String channelId) {
		String channelURL = 
				String.format("https://youtube.com/channel/%s/live", channelId);
		
		YTChannel channel = new YTChannel();
		
		channel.setChannelId(channelId);
		channel.setChannelName(fetchChannelName(channelId));
		channel.setLive(false);
	
		//GET the page and parse it to check if live or not
		try {
			Document doc = Jsoup.connect(channelURL).userAgent("Mozilla").get();

			Element link = doc.selectFirst("link[rel=\"canonical\"]");		
			
			//if url contains "watch" then channel is live, 
			//if it contains "channel" then it is not.
			if (link != null && link.attr("href").contains("watch")) {
				channel.setLive(true);
				
				String streamTitle  = doc.selectFirst("title").text();
				streamTitle = streamTitle.substring(0, streamTitle.lastIndexOf("- YouTube")-1);
				channel.setStreamTitle(streamTitle);
				
				return channel;
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return channel;
	}
	
	
	public static String fetchChannelName(String channelId) {
		String channelURL = 
				String.format("https://youtube.com/channel/%s", channelId);
		String channelName = null;
		
		try {
			Document doc = Jsoup.connect(channelURL).userAgent("Mozilla").get();
			
			Element elem = doc.selectFirst("meta[itemprop=\"name\"]");
			
			if (elem != null) {
				channelName = elem.attr("content");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return channelName;
	}
	
	public static boolean isChannel(String channelId) {
		String channelURL = 
				String.format("https://youtube.com/channel/%s", channelId);
		
		try {
			Jsoup.connect(channelURL).userAgent("Mozilla").get();
		
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
}
