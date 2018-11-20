package me.vrekt.queuesniper.guild;

public class RawGuildConfiguration {

    private String guildId, administratorRoleId, announcementRoleId, announcementChannelId, playerCodesChannelId, countdownChannelId;
    private int countdownTimeout;

    public RawGuildConfiguration() {}

    public RawGuildConfiguration(String guildId, String administratorRoleId, String announcementRoleId, String announcementChannelId, String playerCodesChannelId, String countdownChannelId, int countdownTimeout) {
        this.guildId = guildId;
        this.administratorRoleId = administratorRoleId;
        this.announcementRoleId = announcementRoleId;
        this.announcementChannelId = announcementChannelId;
        this.playerCodesChannelId = playerCodesChannelId;
        this.countdownChannelId = countdownChannelId;
        this.countdownTimeout = countdownTimeout;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public String getAdministratorRoleId() {
        return administratorRoleId;
    }

    public void setAdministratorRoleId(String administratorRoleId) {
        this.administratorRoleId = administratorRoleId;
    }

    public String getAnnouncementRoleId() {
        return announcementRoleId;
    }

    public void setAnnouncementRoleId(String announcementRoleId) {
        this.announcementRoleId = announcementRoleId;
    }

    public String getAnnouncementChannelId() {
        return announcementChannelId;
    }

    public void setAnnouncementChannelId(String announcementChannelId) {
        this.announcementChannelId = announcementChannelId;
    }

    public String getPlayerCodesChannelId() {
        return playerCodesChannelId;
    }

    public void setPlayerCodesChannelId(String playerCodesChannelId) {
        this.playerCodesChannelId = playerCodesChannelId;
    }

    public String getCountdownChannelId() {
        return countdownChannelId;
    }

    public void setCountdownChannelId(String countdownChannelId) {
        this.countdownChannelId = countdownChannelId;
    }

    public int getCountdownTimeout() {
        return countdownTimeout;
    }

    public void setCountdownTimeout(int countdownTimeout) {
        this.countdownTimeout = countdownTimeout;
    }
}
