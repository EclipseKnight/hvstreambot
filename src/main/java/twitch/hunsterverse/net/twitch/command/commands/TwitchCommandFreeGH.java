package twitch.hunsterverse.net.twitch.command.commands;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
			Document doc = Jsoup.connect("https://hunstermonter.net/new_gh_getter.php").get();
			
			String reply = "| ";
						
			Elements ghubs = doc.select("div.EntireGame");
			for (Element el: ghubs) {
				//Game name
				Element games = el.selectFirst("div.GameName");
				//First empty hub
				Element ghub = el.selectFirst("div.emptyGHClass");
				//tag holding the number
				Element num = ghub.selectFirst("b");
				
				String game = "";
				if (games.text().contains("(")) {
					game = games.text().substring(0, games.text().indexOf("(")).stripTrailing();
				} else {
					game = games.text().stripTrailing();
				}
				
				reply += game + ": " + num.text() + " | ";
			}
			
			result = System.currentTimeMillis() - result;
			
			TwitchAPI.sendMessage(event, reply + result + "ms");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

}
