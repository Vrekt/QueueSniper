package me.vrekt.queuesniper.command.commands;

import me.vrekt.queuesniper.command.Command;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.guild.setup.GuildSetupConfiguration;
import me.vrekt.queuesniper.result.MessageActionHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

public class SetupCommand extends Command {

    public SetupCommand(String name, String[] aliases, long cooldown, JDA jda) {
        super(name, aliases, cooldown, jda);
    }

    @Override
    public void execute(String[] args, Member from, TextChannel sentIn, GuildConfiguration configuration) {
        EmbedBuilder permissions = new EmbedBuilder();
        permissions.setTitle("Thanks for using QueueSniper! Please make sure the following permissions are set for QueueSniper:");
        permissions.addField("Text Permissions: ", "**Send Messages**, **Read Messages**, **Read Message History** **Embed Links**", false);
        permissions.addField("Voice Permissions: ", "**Connect**, **Speak**, **Use Voice Activity**", false);
        permissions.addField("General Permissions: ", "**Manage Channels**", false);

        MessageActionHandler.sendMessageToChannel(sentIn, permissions.build()).handleFailure(msg -> failed = true);
        if (failed) {
            return;
        }

        GuildSetupConfiguration setupConfiguration = configuration.getSetupConfiguration();
        setupConfiguration.register(jda, configuration.getGuildId(), sentIn);
    }

}
