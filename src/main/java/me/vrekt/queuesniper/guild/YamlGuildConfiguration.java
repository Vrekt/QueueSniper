package me.vrekt.queuesniper.guild;

/**
 * This class is used to dump a configuration to a YAML file.
 * Since roles and channels are not able to be dumped the IDs are used.
 */
public class YamlGuildConfiguration {

    public int countdownTimeout;
    public String guildId, controlRoleId, announcementRoleId, announcementChannelId, codesChannelId,
            countdownChannelId;

    YamlGuildConfiguration() {
    }

}
