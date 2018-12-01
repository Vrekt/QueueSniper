# QueueSniper
QueueSniper is a discord bot for managing Fortnite queue-snipe matches.

# Features
  - Voice channel countdown (3, 2, 1, GO)
  - Customizable settings such as:
    - Roles to control the bot
    - Roles that will be mentioned when matches are starting
    - What text and voice channels to use to post server codes and countdown in.
  - Keeps track of players and what server they are in and shows that information.
  - Requeue commands to alert players of a requeue

# Planned features
  - Automatic times to start matches
  - More commands and settings

# Screenshots
![alt text](https://i.imgur.com/NYVHhli.png)

![alt text](https://i.imgur.com/BKuC6f2.png)

![alt text](https://i.imgur.com/1twqCrW.png)

# Setting up and adding it to your discord server
You can add QueueSniper to your discord server [here](https://discordapp.com/oauth2/authorize?&client_id=513096941693960223&scope=bot).


Once QueueSniper is in your server run ".setup". Ensure QueueSniper has all the proper permissions required or some features will not work correctly!

# Troubleshooting and help

If the bot is not sending messages or is unable to talk in voice channels:
  - Ensure it has proper permissions (**Send Messages**, **Read Messages**, **Read Message History**)
  - Ensure it has proper voice permissions (**Connect**, **Speak**, **Use Voice Activity**, **Priority Speaker**)
  If it is still not sending messages kick and re-add the bot.
  
If you still need help you can PM me on discord: vrekt#4387

# Self hosting

First you need a few things:
    - JDK 8 or higher installed
    - Have java in your PATH (when installing jdk8 this is done automatically, most of the time).


# Running
  - First head over to the [releases page](https://github.com/Vrekt/QueueSniper/releases) and download the JAR file.
  
  - Next, create a directory to put the JAR file in, QueueSniper when run for the first time creates a few files.
  
  - Then, open command prompt (depending on the directory you may have to run it has administrator) and go into the directory with the    JAR file.
  
  - After that, type 'java -jar QueueSniper_main.jar (your token here)' (do not include the parentheses) if you don't have a discord bot token google how to get one.
  
  - Finally, verify it created the files 'configuration.yaml' and 'database.yaml', these files are used to configure and save guilds the bot as join.


# Example
```
java -jar QueueSniper.jar SIKDH&93743bfhdjDIAS7647283423 
```



# Feedback
If you found an issue or have a suggestion let me know by opening a new issue.
