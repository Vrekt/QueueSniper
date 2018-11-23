package me.vrekt.queuesniper.command.commands;

import me.vrekt.queuesniper.command.Command;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.match.MatchHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.Color;

public class RequeueCommand extends Command {

    private final MatchHandler matchHandler;

    public RequeueCommand(String name, String[] aliases, long cooldown, MatchHandler matchHandler, JDA jda) {
        super(name, aliases, cooldown, jda);
        this.matchHandler = matchHandler;
    }

    @Override
    public boolean execute(String[] args, Member from, TextChannel sentIn, GuildConfiguration configuration) {
        if (!configuration.getRegisterConfiguration().isSetup()) {
            sentIn.sendMessage("You must setup QueueSniper before using this command. Type .setup").queue();
            return false;
        }

        TextChannel channel = configuration.getAnnouncementChannel();

        StringBuilder builder = new StringBuilder();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(new Color(255, 60, 50));
        embedBuilder.setTitle("MATCH REQUEUE, PAY ATTENTION!");

        if (args.length >= 2) {
            for (int i = 1; i < args.length; i++) {
                builder.append(args[i]);
                builder.append(", ");
            }
            embedBuilder.addField("If you are in one of the following servers please re-enter the voice channel '" + configuration.getCountdownChannel().getName() + "'. Another countdown will be started in " + configuration.getTimeout() + " seconds!", builder.toString(), false);
        } else {
            embedBuilder.setTitle("**ALL** players are required to re-queue! Please re-enter the voice channel '" + configuration.getCountdownChannel().getName() + "'. Another countdown will be started in " + configuration.getTimeout() + " seconds!");
        }

        channel.sendMessage(embedBuilder.build()).queue();
        channel.sendMessage(configuration.getAnnouncementRole().getAsMention()).queue();

        embedBuilder.clear();
        embedBuilder.setColor(new Color(192, 160, 110)).addField("A SNIPE MATCH IS STARTING SOON, GET READY!",
                "- A snipe match will start in " + configuration.getTimeout() + " seconds. Please join the voice channel '" +
                        configuration.getCountdownChannel().getName() + "'!",
                false).addField("Instructions: ", "- A 3 second countdown will be started, click Play in-game once you hear 'GO'",
                false);

        channel.sendMessage(embedBuilder.build()).queue();
        matchHandler.queue(configuration);
        return true;
    }
}
