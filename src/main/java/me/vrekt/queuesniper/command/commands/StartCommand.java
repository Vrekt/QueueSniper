package me.vrekt.queuesniper.command.commands;

import me.vrekt.queuesniper.command.Command;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.match.MatchHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.Color;

public class StartCommand extends Command {

    private final MatchHandler matchHandler;

    public StartCommand(String name, String[] aliases, long cooldown, MatchHandler matchHandler, JDA jda) {
        super(name, aliases, cooldown, jda);
        this.matchHandler = matchHandler;
    }

    @Override
    public boolean execute(String[] args, Member from, TextChannel sentIn, GuildConfiguration configuration) {
        if (!configuration.getRegisterConfiguration().isSetup()) {
            sentIn.sendMessage("You must setup QueueSniper before using this command. Type .setup").queue();
            return false;
        }

        if (args.length < 2) {
            sentIn.sendMessage("You must specify a playlist. (.start <solos, duos, squads>)").queue();
            return false;
        }

        EmbedBuilder message = new EmbedBuilder();
        TextChannel channel = configuration.getAnnouncementChannel();

        message.setColor(new Color(192, 160, 110)).addField("A SNIPE MATCH IS STARTING SOON, GET READY!",
                "- A snipe match will start in " + configuration.getTimeout() + " seconds. Please join the voice channel '" +
                        configuration.getCountdownChannel().getName() + "'!",
                false).addField("Instructions: ", "- A 3 second countdown will be started, click Play in-game once you hear 'GO'",
                false).addField("Current playlist: ", "- " + args[1].toUpperCase(), false);

        channel.sendMessage(configuration.getAnnouncementRole().getAsMention()).queue();
        channel.sendMessage(message.build()).queue();
        matchHandler.queue(configuration);
        return true;
    }

}
