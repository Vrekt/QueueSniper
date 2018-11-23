package me.vrekt.queuesniper.command.commands;

import me.vrekt.queuesniper.command.Command;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.guild.register.GuildRegisterConfiguration;
import me.vrekt.queuesniper.guild.register.GuildRegistrationWatcher;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

public class SetupCommand extends Command {

    public SetupCommand(String name, String[] aliases, long cooldown, JDA jda) {
        super(name, aliases, cooldown, jda);
    }

    @Override
    public boolean execute(String[] args, Member from, TextChannel sentIn, GuildConfiguration configuration) {
        EmbedBuilder permissions = new EmbedBuilder();
        permissions.setTitle("Thanks for using QueueSniper! Please make sure the following permissions are set for QueueSniper:");
        permissions.addField("Text Permissions: ", "**Send Messages**, **Read Messages**, **Read Message History**", false);
        permissions.addField("Voice Permissions: ", "**Connect**, **Speak**, **Use Voice Activity**, **Priority Speaker**", false);
        permissions.addField("General Permissions: ", "**Manage Channels**", false);
        sentIn.sendMessage(permissions.build()).queue();

        GuildRegisterConfiguration registerConfiguration = configuration.getRegisterConfiguration();
        GuildRegistrationWatcher watcher = registerConfiguration.getWatcher();

        if (watcher == null) {
            registerConfiguration.setSetup(false);
            GuildRegistrationWatcher newWatcher = new GuildRegistrationWatcher(sentIn, from, registerConfiguration);
            newWatcher.addListener();
            registerConfiguration.setWatcher(newWatcher);
        }

        sentIn.sendMessage(registerConfiguration.checkAndReturnOutput(null, sentIn.getGuild(), configuration)).queue();
        return true;
    }

}
