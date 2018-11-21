package me.vrekt.queuesniper.listener;

import me.vrekt.queuesniper.guild.GuildConfiguration;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class GuildListener {

    @SubscribeEvent
    public void onGuildJoin(GuildJoinEvent event) {
        Guild guild = event.getGuild();
        GuildConfiguration.add(new GuildConfiguration(guild.getId()).setGuild(guild).setSelf(guild.getSelfMember()));
    }

    @SubscribeEvent
    public void onGuildLeave(GuildLeaveEvent event) {
        // TODO DATABASE STUFF
    }

}
