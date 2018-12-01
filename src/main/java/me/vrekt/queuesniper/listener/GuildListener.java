package me.vrekt.queuesniper.listener;

import me.vrekt.queuesniper.QSLogger;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.guild.GuildConfigurationBuilder;
import me.vrekt.queuesniper.guild.GuildConfigurationFactory;
import me.vrekt.queuesniper.guild.setup.GuildSetupConfiguration;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class GuildListener {

    @SubscribeEvent
    public void onGuildJoin(GuildJoinEvent event) {
        Guild guild = event.getGuild();

        // set up the basics needed to work without setup
        GuildConfiguration configuration = new GuildConfigurationBuilder(guild.getId(), guild, guild.getSelfMember(),
                guild.getPublicRole()).build();
        configuration.setRegisterConfiguration(new GuildSetupConfiguration(false, configuration));

        GuildConfigurationFactory.add(configuration);
        QSLogger.log(configuration, "QueueSniper has joined!");
    }

}
