package me.vrekt.queuesniper.listener.guild;

import me.vrekt.queuesniper.QSLogger;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.guild.GuildConfigurationFactory;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class GuildListener {

    @SubscribeEvent
    public void onGuildJoin(GuildJoinEvent event) {
        Guild guild = event.getGuild();
        GuildConfiguration configuration = new GuildConfiguration(guild.getId(), guild, guild.getSelfMember(), guild.getPublicRole());
        GuildConfigurationFactory.add(configuration);

        QSLogger.log(configuration, "QueueSniper has been added to this guild!");
    }

}
