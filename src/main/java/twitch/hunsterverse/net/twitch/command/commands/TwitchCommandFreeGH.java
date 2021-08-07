package twitch.hunsterverse.net.twitch.command.commands;

import java.io.IOException;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import twitch.hunsterverse.net.twitch.TwitchBot;
import twitch.hunsterverse.net.twitch.command.TwitchCommand;
import twitch.hunsterverse.net.twitch.command.TwitchCommandEvent;
import twitch.hunsterverse.net.twitch.features.TwitchAPI;

public class TwitchCommandFreeGH extends TwitchCommand {

	public TwitchCommandFreeGH() {
		this.feature = "twitch_command_free_gh";
		this.name = "freegh";
	}
	
	@Override
	protected void execute(TwitchCommandEvent event) {
		long result = System.currentTimeMillis();
		
		try {
			Document doc = Jsoup.connect("https://api.hunsterverse.net/v1/halls/MH/freegh")
					.ignoreContentType(true)
					.header("Authorization", "HV " + TwitchBot.configuration.getApi().get("hunsterverse_api_key")).get();
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode payloadNode = mapper.readTree(doc.text()).get("payload");
			
			String reply = "\n ";
			
			Iterator<String> it = payloadNode.fieldNames();
			while (it.hasNext()) {
				String game = it.next();
				JsonNode node = payloadNode.get(game);
				
				if (node.isContainerNode()) {
					reply += String.format("%s: [A: %s B: %s C: %s] \n ",
							game, node.get("A").asText(), node.get("B").asText(), node.get("C").asText());
				}
				
				if (!node.isContainerNode() && node.isTextual()) {
					reply += String.format("%s: %s",
							game, node.asText());
				}
			}
			
			result = System.currentTimeMillis() - result;
			
			TwitchAPI.sendMessage(event, reply + " | " + result + "ms");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

}
