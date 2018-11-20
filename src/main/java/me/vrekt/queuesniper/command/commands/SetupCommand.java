package me.vrekt.queuesniper.command.commands;

import me.vrekt.queuesniper.command.Command;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.guild.setup.GuildSetupConfiguration;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

public class SetupCommand extends Command {

    public SetupCommand(String name, String[] aliases, long cooldown, JDA jda) {
        super(name, aliases, cooldown, jda);
    }

    @Override
    public void execute(String[] args, Member from, TextChannel sentIn, GuildConfiguration configuration) {
        final GuildSetupConfiguration temporaryConfiguration = configuration.getSetupConfiguration();
        if (configuration.getGuild() == null) {
            configuration.setGuild(sentIn.getGuild());
        }
        if (temporaryConfiguration == null) {
            GuildSetupConfiguration setupConfiguration = new GuildSetupConfiguration(sentIn, from);
            configuration.setGuildSetupConfiguration(setupConfiguration);
            jda.addEventListener(setupConfiguration);

            sentIn.sendMessage(setupConfiguration.checkAndReturnOutput(null, sentIn.getGuild(), configuration)).queue();
        } else {
            sentIn.sendMessage(temporaryConfiguration.checkAndReturnOutput(null, sentIn.getGuild(), configuration)).queue();
        }
    }
}
