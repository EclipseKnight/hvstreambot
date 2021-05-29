# Hunsterverse Stream Bot

This is a stream bot application design for the discord server Hunsterverse.


## Requirements
- jdk 15 or openjdk 15 


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
!s check [<@discorduser>, <twitchchannel>] - displays known information regarding the passed argument i.e. user association, affiliation, etc
!s link <@discorduser> <twitchchannel> [<affiliate> true, false] - links user and channel and designate if they are affilite or not
!s unlink <discordId or mention> - unlinks user.
!s islive <twitchchannel> - checks if channel is live.
!s embedUpdate - updates the live stream active embeds.
!s backup - initiates a database backup manually. (Does not affect scheduled backups)
```

More information in the discordbot.yaml and twitchbot.yaml files.

## Features

- Active embed for tracking live streamers.
- Ability to track link users to a twitch account and actively track their status (live or offline). Stored in a JSON formatted database
- Manual and scheduled database backups and path configuration.
- full feature configuration. Channel usage, role usage, ability to enable and disable certain commands. 
- Affiliate parameter to allow affiliate only command usage. Implemented for use with future expansions to this bot.
## Other tools

[hvstreambotupdater](https://github.com/EclipseKnight/hvstreambotupdater) - lets you update the bot by fetching the latest release from the repo. Update command is disabled due to inconsistencies between OS
