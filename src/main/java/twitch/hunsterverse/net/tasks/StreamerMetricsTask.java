package twitch.hunsterverse.net.tasks;

import java.util.List;

import net.dv8tion.jda.api.JDA.Status;
import twitch.hunsterverse.net.database.JsonDB;
import twitch.hunsterverse.net.database.documents.HVStreamer;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.commands.CommandUtils;
import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;
import twitch.hunsterverse.net.twitch.TwitchUtils;

public class StreamerMetricsTask implements Runnable {
	
	private long rate = 300000;
	
	private long firstTime;
	private long lastTime;
	private long passedTime;
	private long unprocessedTime;
	
	private boolean run;
	
	@Override
	public void run() {
		
		firstTime = 0;
		lastTime = System.currentTimeMillis();
		passedTime = 0;
		unprocessedTime = 0;
		
		run = true;
		
		while (run && (DiscordBot.jda.getStatus() != Status.SHUTDOWN && DiscordBot.jda.getStatus() != Status.SHUTTING_DOWN)) {
			firstTime = System.currentTimeMillis();
			passedTime = firstTime - lastTime;
			lastTime = firstTime;
			
			unprocessedTime += passedTime;
			while (unprocessedTime >= rate) {
				List<String> channels = TwitchUtils.getLiveFilteredChannels();
				//Add time to streamers.
				for (String ch: channels) {
					HVStreamer s = CommandUtils.getStreamerWithTwitchChannel(ch);
					s.setTimeStreamed((s.getTimeStreamed() + unprocessedTime));
					JsonDB.database.upsert(s);
				}
				
				unprocessedTime -= rate;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Logger.log(Level.WARN, Thread.currentThread().getName() + " was interrupted while sleeping.\n Exiting loop.");
				run = false;
			}
		}
		
		Logger.log(Level.WARN, "StreamerMetricsTask has finished.");
	}
	
	
	public long getUnprocessedTime() {
		return unprocessedTime;
	}
	
	public long getRate() {
		return rate;
	}

}
