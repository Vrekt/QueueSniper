package me.vrekt.queuesniper.command.commands;

import me.vrekt.queuesniper.command.Command;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.match.GuildMatchHandler;
import me.vrekt.queuesniper.result.MessageActionHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.Color;

public class StartCommand extends Command {

    private final GuildMatchHandler matchHandler;

    public StartCommand(String name, String[] aliases, long cooldown, GuildMatchHandler matchHandler) {
        super(name, aliases, cooldown);
        this.matchHandler = matchHandler;
    }

    @Override
    public void execute(String[] args, Member from, TextChannel sentIn, GuildConfiguration configuration) {
        if (!configuration.getSetupConfiguration().isSetup()) {
            MessageActionHandler.sendMessageToChannel(sentIn, "QueueSniper must be setup before executing this command. Type .setup to " +
                    "get started");
            failed = true;
            return;
        }

        if (args.length < 2) {
            MessageActionHandler.sendMessageToChannel(sentIn, "You must specify a playlist. (.start <solos, duos, squads>)");
            failed = true;
            return;
        }

        EmbedBuilder message = new EmbedBuilder();
        TextChannel channel = configuration.getAnnouncementChannel();

        message.setColor(new Color(192, 160, 110)).addField("A SNIPE MATCH IS STARTING SOON, GET READY!",
                "- A snipe match will start in " + configuration.getCountdownTimeout() + " seconds. Please join the voice channel '" +
                        configuration.getCountdownChannel().getName() + "'!",
                false).addField("Instructions: ", "- A 3 second countdown will be started, click Play in-game once you hear 'GO'",
                false).addField("Current playlist: ", "- " + args[1].toUpperCase(), false);

        MessageActionHandler.sendMessageToChannel(channel, configuration.getAnnouncementRole().getAsMention()).handleFailure(msg -> failed = true);
        MessageActionHandler.sendMessageToChannel(channel, message.build()).handleFailure(msg -> failed = true);

        if (failed) {
            return;
        }

        matchHandler.queueMatch(configuration);
    }
}
