package twitch.hunsterverse.net.twitch.command;

public abstract class TwitchCommand {

	
	protected String feature = "null";
	
	/**
	 * The name of the command, allows the command to be called via: {@code [prefix]<command name>}.
	 * 
	 */
	protected String name = "null";
	
	/**
	 * An array of aliases for the command.
	 */
	protected String[] aliases = new String[0];
	
	
	protected abstract void execute(TwitchCommandEvent event);
	
	
	public String getFeature() {
		return feature;
	}
	
	public String getName() {
		return name;
	}
	
	public String[] getAliases() {
		return aliases;
	}
}
