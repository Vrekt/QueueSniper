package me.vrekt.queuesniper.command.commands;

import me.vrekt.queuesniper.command.Command;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.Color;

public class HelpCommand extends Command {

    public HelpCommand(String name, String[] aliases, long cooldown, JDA jda) {
        super(name, aliases, cooldown, jda);
    }

    @Override
    public boolean execute(String[] args, Member from, TextChannel sentIn, GuildConfiguration configuration) {
        EmbedBuilder message = new EmbedBuilder();
        message.setColor(new Color(64, 64, 64)).setAuthor("QueueSniper help").setTitle("Administrator commands: ");

        message.addField(".setup", "- This command is used to setup QueueSniper.", false);
        message.addField(".start <playlist>", "- This is the default command to start snipe matches.", false);
        message.addField(".requeue <server ids>", " - This command will alert players that they should requeue. If no server ids are " +
                "entered, players will be notified it is a full requeue.", false);

        EmbedBuilder permissions = new EmbedBuilder();
        permissions.setTitle("Required permissions: ");
        permissions.addField("Text Permissions: ", "**Send Messages**, **Read Messages**, **Read Message History**", false);
        permissions.addField("Voice Permissions: ", "**Connect**, **Speak**, **Use Voice Activity**, **Priority Speaker**", false);
        permissions.addField("General Permissions: ", "**Manage Channels**", false);

        sentIn.sendMessage(permissions.build()).queue();
        sentIn.sendMessage(message.build()).queue();
        return true;
    }
}
