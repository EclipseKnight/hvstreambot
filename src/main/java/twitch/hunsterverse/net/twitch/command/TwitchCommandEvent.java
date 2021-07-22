package twitch.hunsterverse.net.twitch.command;

import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.philippheuer.events4j.api.service.IServiceMediator;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.chat.flag.AutoModFlag;
import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.common.util.ChatReply;

public class TwitchCommandEvent {

	private final ChannelMessageEvent event;
	private String args;
	
	public TwitchCommandEvent(ChannelMessageEvent event, String args) {
		this.event = event;
		this.args = args;
	}
	
	
	public String getArgs() {
		return args;
	}
	
	/*All of the getters and setters for ChannelMessageEvent */
	public String getMessage() {
		return event.getMessage();
	}
	
	public IRCMessageEvent getMessageEvent() {
		return event.getMessageEvent();
	}
	
	public String getNonce() {
		return event.getNonce();
	}
	
	public void timeout(String user, Duration duration, String reason) {
		event.timeout(user, duration, reason);
	}
	
	public void ban(String user, String reason) {
		event.ban(user, reason);
	}
	
	public EventChannel getChannel() {
		return event.getChannel();
	}
	
	public Optional<String> getCustomRewardId(){
		return event.getCustomRewardId();
	}
	
	public String getEventId() {
		return event.getEventId();
	}
	
	public Calendar getFiredAt() {
		return event.getFiredAt();
	}
	
	public Instant getFiredAtInstant() {
		return event.getFiredAtInstant();
	}
	
	public List<AutoModFlag> getFlags() {
		return event.getFlags();
	}
	
	public Set<CommandPermission> getPermissions() {
		return event.getPermissions();
	}
	
	public ChatReply getReplyInfo() {
		return event.getReplyInfo();
	}
	
	public IServiceMediator getServiceMediator() {
		return event.getServiceMediator();
	}
	
	public int getSubscriberMonths() {
		return event.getSubscriberMonths();
	}
	
	public int SubscriptionTier() {
		return event.getSubscriptionTier();
	}
	
	public TwitchChat getTwitchChat() {
		return event.getTwitchChat();
	}
	
	public EventUser getUser() {
		return event.getUser();
	}
	
	public boolean isHighlightedMessage() {
		return event.isHighlightedMessage();
	}
	
	public boolean isSkipSubsModeMessage() {
		return event.isSkipSubsModeMessage();
	}
	
	public void setEventId(String eventId) {
		event.setEventId(eventId);
	}
	
	public void setFiredAt(Calendar calendar) {
		event.setFiredAt(calendar);
	}
	
	public void setFiredAtInstant(Instant firedAtInstant) {
		event.setFiredAtInstant(firedAtInstant);
	}
	
	public void setServiceMediator(IServiceMediator serviceMediator) {
		event.setServiceMediator(getServiceMediator());
	}
	
}

