package me.vrekt.queuesniper.guild;

import me.vrekt.queuesniper.guild.dump.DumpableGuildConfiguration;
import me.vrekt.queuesniper.guild.register.GuildRegisterConfiguration;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class GuildConfiguration {

    private final String guildId;
    private final Guild guild;
    private final Member self;

    private Role publicRole;
    private Role controlRole;
    private Role announcementRole;

    private TextChannel announcementChannel;
    private TextChannel codesChannel;
    private VoiceChannel countdownChannel;

    private int timeout;
    private GuildRegisterConfiguration registerConfiguration = new GuildRegisterConfiguration(false);

    public GuildConfiguration(String guildId, Guild guild, Member self, Role publicRole) {
        this.guildId = guildId;
        this.guild = guild;
        this.self = self;
        this.publicRole = publicRole;
    }

    public GuildRegisterConfiguration getRegisterConfiguration() {
        return registerConfiguration;
    }

    public void setRegisterConfiguration(GuildRegisterConfiguration registerConfiguration) {
        this.registerConfiguration = registerConfiguration;
    }

    public String getGuildId() {
        return guildId;
    }

    public Guild getGuild() {
        return guild;
    }

    public Member getSelf() {
        return self;
    }

    public Role getPublicRole() {
        return publicRole;
    }

    public Role getControlRole() {
        return controlRole;
    }

    public GuildConfiguration setControlRole(Role role) {
        this.controlRole = role;
        return this;
    }

    public Role getAnnouncementRole() {
        return announcementRole;
    }

    public GuildConfiguration setAnnouncementRole(Role role) {
        this.announcementRole = role;
        return this;
    }

    public TextChannel getAnnouncementChannel() {
        return announcementChannel;
    }

    public GuildConfiguration setAnnouncementChannel(TextChannel channel) {
        this.announcementChannel = channel;
        return this;
    }

    public TextChannel getCodesChannel() {
        return codesChannel;
    }

    public GuildConfiguration setCodesChannel(TextChannel channel) {
        this.codesChannel = channel;
        return this;
    }

    public VoiceChannel getCountdownChannel() {
        return countdownChannel;
    }

    public GuildConfiguration setCountdownChannel(VoiceChannel channel) {
        this.countdownChannel = channel;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public DumpableGuildConfiguration dump() {
        DumpableGuildConfiguration dump = new DumpableGuildConfiguration();

        dump.guildId = guildId;
        dump.controlRoleId = controlRole.getId();
        dump.announcementRoleId = announcementRole.getId();
        dump.announcementChannelId = announcementChannel.getId();
        dump.codesChannelId = codesChannel.getId();
        dump.countdownChannelId = countdownChannel.getId();
        dump.timeout = timeout;
        return dump;
    }

}
