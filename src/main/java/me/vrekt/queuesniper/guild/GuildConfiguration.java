package me.vrekt.queuesniper.guild;

import me.vrekt.queuesniper.guild.setup.GuildSetupConfiguration;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class GuildConfiguration {

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
    private GuildSetupConfiguration setupConfiguration;

    public GuildConfiguration(GuildConfigurationBuilder builder) {
        this.guildId = builder.getGuildId();
        this.guild = builder.getGuild();
        this.self = builder.getSelf();
        this.publicRole = builder.getPublicRole();

        this.controlRole = builder.getControlRole();
        this.announcementRole = builder.getAnnouncementRole();
        this.announcementChannel = builder.getAnnouncementChannel();
        this.codesChannel = builder.getCodesChannel();
        this.countdownChannel = builder.getCountdownChannel();

        this.countdownTimeout = builder.getCountdownTimeout();
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

    public void setControlRole(Role controlRole) {
        this.controlRole = controlRole;
    }

    public Role getAnnouncementRole() {
        return announcementRole;
    }

    public void setAnnouncementRole(Role announcementRole) {
        this.announcementRole = announcementRole;
    }

    public TextChannel getAnnouncementChannel() {
        return announcementChannel;
    }

    public void setAnnouncementChannel(TextChannel announcementChannel) {
        this.announcementChannel = announcementChannel;
    }

    public TextChannel getCodesChannel() {
        return codesChannel;
    }

    public void setCodesChannel(TextChannel codesChannel) {
        this.codesChannel = codesChannel;
    }

    public VoiceChannel getCountdownChannel() {
        return countdownChannel;
    }

    public void setCountdownChannel(VoiceChannel countdownChannel) {
        this.countdownChannel = countdownChannel;
    }

    public int getCountdownTimeout() {
        return countdownTimeout;
    }

    public void setCountdownTimeout(int countdownTimeout) {
        this.countdownTimeout = countdownTimeout;
    }

    public GuildSetupConfiguration getSetupConfiguration() {
        return setupConfiguration;
    }

    public void setRegisterConfiguration(GuildSetupConfiguration setupConfiguration) {
        this.setupConfiguration = setupConfiguration;
    }

    public YamlGuildConfiguration dump() {
        YamlGuildConfiguration dump = new YamlGuildConfiguration();

        // ignore if any fields are null
        if (guildId == null || controlRole == null || announcementRole == null || announcementChannel == null || codesChannel == null || countdownChannel == null) {
            return null;
        }

        dump.guildId = guildId;
        dump.controlRoleId = controlRole.getId();
        dump.announcementRoleId = announcementRole.getId();
        dump.announcementChannelId = announcementChannel.getId();
        dump.codesChannelId = codesChannel.getId();
        dump.countdownChannelId = countdownChannel.getId();
        dump.countdownTimeout = countdownTimeout;
        return dump;
    }

}
