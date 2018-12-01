package me.vrekt.queuesniper.guild;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 * This class is used to build a guild configuration
 */
public class GuildConfigurationBuilder {

    private final String guildId;
    private final Guild guild;
    private final Member self;

    private final Role publicRole;
    private Role controlRole;
    private Role announcementRole;

    private TextChannel announcementChannel;
    private TextChannel codesChannel;
    private VoiceChannel countdownChannel;

    private int countdownTimeout;

    public GuildConfigurationBuilder(String guildId, Guild guild, Member self, Role publicRole) {
        this.guildId = guildId;
        this.guild = guild;
        this.self = self;
        this.publicRole = publicRole;
    }

    String getGuildId() {
        return guildId;
    }

    public Guild getGuild() {
        return guild;
    }

    Member getSelf() {
        return self;
    }

    Role getPublicRole() {
        return publicRole;
    }

    public Role getControlRole() {
        return controlRole;
    }

    public GuildConfigurationBuilder setControlRole(Role controlRole) {
        this.controlRole = controlRole;
        return this;
    }

    Role getAnnouncementRole() {
        return announcementRole;
    }

    public GuildConfigurationBuilder setAnnouncementRole(Role announcementRole) {
        this.announcementRole = announcementRole;
        return this;
    }

    TextChannel getAnnouncementChannel() {
        return announcementChannel;
    }

    public GuildConfigurationBuilder setAnnouncementChannel(TextChannel announcementChannel) {
        this.announcementChannel = announcementChannel;
        return this;
    }

    TextChannel getCodesChannel() {
        return codesChannel;
    }

    public GuildConfigurationBuilder setCodesChannel(TextChannel codesChannel) {
        this.codesChannel = codesChannel;
        return this;
    }

    VoiceChannel getCountdownChannel() {
        return countdownChannel;
    }

    public GuildConfigurationBuilder setCountdownChannel(VoiceChannel countdownChannel) {
        this.countdownChannel = countdownChannel;
        return this;
    }

    int getCountdownTimeout() {
        return countdownTimeout;
    }

    public GuildConfigurationBuilder setCountdownTimeout(int countdownTimeout) {
        this.countdownTimeout = countdownTimeout;
        return this;
    }

    public GuildConfiguration build() {
        return new GuildConfiguration(this);
    }

}
