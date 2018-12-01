package me.vrekt.queuesniper.command.commands;

import me.vrekt.queuesniper.command.Command;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.match.GuildMatchHandler;
import me.vrekt.queuesniper.result.MessageActionHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.Color;

public class RequeueCommand extends Command {

    private final GuildMatchHandler matchHandler;

    public RequeueCommand(String name, String[] aliases, long cooldown, GuildMatchHandler matchHandler) {
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
            embedBuilder.addField("If you are in one of the following servers please re-enter the voice channel '" + configuration.getCountdownChannel().getName() + "'. Another countdown will be started in " + configuration.getCountdownTimeout() + " seconds!", builder.toString(), false);
        } else {
            embedBuilder.setTitle("**ALL** players are required to re-queue! Please re-enter the voice channel '" + configuration.getCountdownChannel().getName() + "'. Another countdown will be started in " + configuration.getCountdownTimeout() + " seconds!");
        }

        MessageActionHandler.sendMessageToChannel(channel, embedBuilder.build());
        MessageActionHandler.sendMessageToChannel(channel, configuration.getAnnouncementRole().getAsMention());

        embedBuilder.clear();
        embedBuilder.setColor(new Color(192, 160, 110)).addField("A SNIPE MATCH IS STARTING SOON, GET READY!",
                "- A snipe match will start in " + configuration.getCountdownTimeout() + " seconds. Please join the voice channel '" +
                        configuration.getCountdownChannel().getName() + "'!",
                false).addField("Instructions: ", "- A 3 second countdown will be started, click Play in-game once you hear 'GO'",
                false);

        MessageActionHandler.sendMessageToChannel(configuration.getAnnouncementChannel(), embedBuilder.build()).handleFailure(msg -> failed = true);
        if (failed) {
            return;
        }

        matchHandler.queueMatch(configuration);
    }

}
