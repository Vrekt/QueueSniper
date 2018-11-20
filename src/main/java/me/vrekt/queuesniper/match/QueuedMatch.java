package me.vrekt.queuesniper.match;

import me.vrekt.queuesniper.guild.GuildConfiguration;

public class QueuedMatch {

    private final GuildConfiguration guildConfiguration;
    private final Playlist playlist;
    private final long timeQueued;

    public QueuedMatch(GuildConfiguration guildConfiguration, Playlist playlist) {
        this.guildConfiguration = guildConfiguration;
        this.playlist = playlist;

        timeQueued = System.currentTimeMillis();
    }

    boolean ready() {
        return System.currentTimeMillis() - timeQueued >= guildConfiguration.getCountdownTimeout() * 1000;
    }

    GuildConfiguration getGuildConfiguration() {
        return guildConfiguration;
    }
}
