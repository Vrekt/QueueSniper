package me.vrekt.queuesniper.listener;

import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.guild.setup.GuildSetupConfiguration;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class GuildListener {

    @SubscribeEvent
    public void onGuildJoin(GuildJoinEvent event) {
        Guild guild = event.getGuild();
        GuildConfiguration configuration =
                new GuildConfiguration(guild.getId(), guild, guild.getSelfMember(), guild.getPublicRole()).setGuildSetupConfiguration(new GuildSetupConfiguration(false));
        GuildConfiguration.add(configuration);
    }

    @SubscribeEvent
    public void onGuildLeave(GuildLeaveEvent event) {
        // TODO DATABASE STUFF
    }

}
