package twitch.hunsterverse.net.database.documents;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "active_embeds", schemaVersion = "1.0")
public class ActiveEmbed {

	@Id
	private String messageId;
	
	public String getMessageId() {
		return messageId;
	}
	
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
}
