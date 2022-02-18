package twitch.hunsterverse.net.tasks;

import net.dv8tion.jda.api.JDA.Status;
import twitch.hunsterverse.net.discord.DiscordBot;
import twitch.hunsterverse.net.discord.DiscordUtils;
import twitch.hunsterverse.net.logger.Logger;
import twitch.hunsterverse.net.logger.Logger.Level;

public class UpdateLiveEmbedsTask implements Runnable {

	private long delay;
	
	public UpdateLiveEmbedsTask(long delay) {
		this.delay = delay;
	}
	
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
			
			while (unprocessedTime >= delay) {
				unprocessedTime -= delay;
				DiscordUtils.updateLiveEmbeds(true);
			}
			
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				Logger.log(Level.WARN, Thread.currentThread().getName() + " was interrupted while sleeping.\n Exiting loop.");
				run = false;
			}
		}
		
		Logger.log(Level.WARN, "UpdateLiveEmbedTask has finished.");
	}

}
