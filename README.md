# Hunsterverse Stream Bot

This is a stream bot application design for the discord server Hunsterverse.


## Requirements
- Orace JDK 15 or OpenJDK 15 minimum


[Latest openjdk](https://jdk.java.net/)

[Latest Oracle JDK](https://www.oracle.com/java/technologies/javase-downloads.html)

Note that thanks to backwards compatability, the latest jdk will run older versions just fine. 

For linux installation of openjdk [read here](https://openjdk.java.net/install/) for more info... [or here](https://www.linuxuprising.com/2020/09/how-to-install-oracle-java-15-on-ubuntu.html)... and [here](https://aboullaite.me/switching-between-java-versions-on-ubuntu-linux/) for info on how to manage multiple installed versions. Just look up how for your specific distro.
## Usage

```bash
java -jar hvstreambot.jar
```
### Discord usage

```
!s help
```
This will display all available commands the user is allowed to use.

```
!s config [(list, ls), reload] - reloads the bot config.
!s restart [twitch, discord] - restarts twitch or discord bot.
!s embedUpdate - updates the live stream active embeds.
!s check [<@discorduser>, <twitchchannel>] - displays known information regarding the passed argument i.e. user association, affiliation, etc
!s link <@discorduser> <twitchchannel> [<affiliate> true, false] - links user and channel and designate if they are affilite or not
!s unlink <discordId or mention> - unlinks user.
!s islive <twitchchannel> - checks if channel is live.
!s backup - initiates a database backup manually. (Does not affect scheduled backups)

!s subscribe <@streamer or discordid> - subscribes to streamer.
!s unsubscribe <@streamer or discordid> - unsubscribes to streamer.
!s togglenotifs - mutes/unmutes all subscriber notifications.
!s subscriptions - who you are subscribed to.

!s gamefilter [create, delete, list, select] - manage filters.

```

More information in the discordbot.yaml and twitchbot.yaml files.

## Features

- Active embed for tracking live streamers.
- Ability to track link users to a twitch account and actively track their status (live or offline). Stored in a JSON formatted database
- Manual and scheduled database backups and path configuration.
- full feature configuration. Channel usage, role usage, ability to enable and disable certain commands. 
- Affiliate parameter to allow affiliate only command usage. Implemented for use with future expansions to this bot.
- Allow users to subscribe to get a discord dm (notification) when the streamer goes live. Also ability to mute notifs
- Streamers can create and set filters to allow a stream get posted to an embed when they play a certain game. 

## Other tools

[hvstreambotupdater](https://github.com/EclipseKnight/hvstreambotupdater) - lets you update the bot by fetching the latest release from the repo. Update command is disabled due to inconsistencies between OS
