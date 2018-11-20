package me.vrekt.queuesniper.command.commands;

import me.vrekt.queuesniper.command.Command;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.match.MatchQueueHandler;
import me.vrekt.queuesniper.match.Playlist;
import me.vrekt.queuesniper.match.QueuedMatch;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.Color;

public class StartCommand extends Command {

    private final MatchQueueHandler queueHandler;

    public StartCommand(String name, String[] aliases, long cooldown, MatchQueueHandler queueHandler, JDA jda) {
        super(name, aliases, cooldown, jda);
        this.queueHandler = queueHandler;
    }

    @Override
    public void execute(String[] args, Member from, TextChannel sentIn, GuildConfiguration configuration) {
        if (args.length < 2) {
            sentIn.sendMessage("You must specify a playlist. (.start <solos, duos, squads>)").queue();
            return;
        }

        if (configuration.getSetupConfiguration() == null || !configuration.getSetupConfiguration().isSetup()) {
            sentIn.sendMessage("You must setup QueueSniper before using this command (.setup)").queue();
            return;
        }

        Playlist playlist;
        try {
            playlist = Playlist.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException exception) {
            sentIn.sendMessage(args[1] + " is an invalid playlist!").queue();
            return;
        }

        EmbedBuilder message = new EmbedBuilder();
        TextChannel channel = configuration.getAnnouncementChannel();
        message.setColor(new Color(192, 160, 110)).addField("A SNIPE MATCH IS STARTING SOON, GET READY!",
                "- A snipe match will start in " + configuration.getCountdownTimeout() + " seconds. Please join the voice channel '" +
                        configuration.getCountdownChannel().getName() + "'!",
                false).addField("Instructions: ", "- A 3 second countdown will be started, click Play in-game once you hear 'GO'",
                false).addField("Current playlist: ", "- " + playlist.name(), false);
        channel.sendMessage(message.build()).queue();
        channel.sendMessage(configuration.getAnnouncementRole().getAsMention()).queue();
        queueHandler.queue(configuration, new QueuedMatch(configuration, playlist));
    }
}
