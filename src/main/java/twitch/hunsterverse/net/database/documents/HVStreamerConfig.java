package twitch.hunsterverse.net.database.documents;

import java.util.List;
import java.util.Map;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "streamer_configs", schemaVersion = "1.0")
public class HVStreamerConfig {

	@Id
	String discordId;
	
	String selectedFilter;
	
	Map<String, List<String>> gameFilters;
	
	public String getDiscordId() {
		return discordId;
	}
	
	public void setDiscordId(String discordId) {
		this.discordId = discordId;
	}
	
	public String getSelectedFilter() {
		return selectedFilter;
	}
	
	public void setSelectedFilter(String selectedFilter) {
		this.selectedFilter = selectedFilter;
	}
	
	public Map<String, List<String>> getGameFilters() {
		return gameFilters;
	}
	
	public void setGameFilters(Map<String, List<String>> gameFilters) {
		this.gameFilters = gameFilters;
	}
	
}
