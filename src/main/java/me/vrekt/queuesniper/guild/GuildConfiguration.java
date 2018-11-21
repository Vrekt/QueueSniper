package me.vrekt.queuesniper.guild;

import me.vrekt.queuesniper.guild.setup.GuildSetupConfiguration;
import me.vrekt.queuesniper.utility.CheckUtility;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GuildConfiguration implements IGuildConfiguration {
    private static final ConcurrentHashMap<String, GuildConfiguration> CONFIGURATION_MAP = new ConcurrentHashMap<>();

    private final String guildId;
    private Guild guild;
    private Member self;

    private GuildSetupConfiguration setupConfiguration;

    private Role administratorRole;
    private Role announcementRole;
    private Role publicRole;

    private TextChannel announcementChannel;
    private TextChannel playerCodesChannel;
    private VoiceChannel countdownChannel;

    private int countdownTimeout;

    public GuildConfiguration(String guildId) {
        this.guildId = guildId;
    }

    /**
     * Retrieve the GuildConfiguration.
     *
     * @param id the ID of the guild.
     * @return the GuildConfiguration, a new one is created if it doesn't already exist.
     */
    public static GuildConfiguration getFromId(String id) {
        if (CONFIGURATION_MAP.containsKey(id)) {
            return CONFIGURATION_MAP.get(id);
        }
        GuildConfiguration configuration = new GuildConfiguration(id);
        CONFIGURATION_MAP.put(id, configuration);
        return configuration;
    }

    /**
     * Add a guild configuration to the map
     *
     * @param configuration the guild configuration.
     */
    public static void add(GuildConfiguration configuration) {
        if (CONFIGURATION_MAP.containsKey(configuration.getGuildId())) {
            return;
        }
        CONFIGURATION_MAP.put(configuration.getGuildId(), configuration);
    }

    /**
     * @return the whole map of guild configurations.
     */
    public static Map<String, GuildConfiguration> getConfigurationMap() {
        return CONFIGURATION_MAP;
    }

    public Guild getGuild() {
        return guild;
    }

    @Override
    public GuildConfiguration setGuild(Guild guild) {
        this.guild = guild;
        return this;
    }

    public Member getSelf() {
        return self;
    }

    @Override
    public GuildConfiguration setSelf(Member self) {
        this.self = self;
        return this;
    }

    public GuildSetupConfiguration getSetupConfiguration() {
        return setupConfiguration;
    }

    @Override
    public GuildConfiguration setGuildSetupConfiguration(GuildSetupConfiguration setupConfiguration) {
        this.setupConfiguration = setupConfiguration;
        return this;
    }

    public String getGuildId() {
        return guildId;
    }

    public Role getAdministratorRole() {
        return administratorRole;
    }

    @Override
    public GuildConfiguration setAdministratorRole(Role administratorRole) {
        this.administratorRole = administratorRole;
        return this;
    }

    public Role getAnnouncementRole() {
        return announcementRole;
    }

    @Override
    public GuildConfiguration setAnnouncementRole(Role announcementRole) {
        this.announcementRole = announcementRole;
        return this;
    }

    public Role getPublicRole() {
        return publicRole;
    }

    @Override
    public GuildConfiguration setPublicRole(Role publicRole) {
        this.publicRole = publicRole;
        return this;
    }

    public TextChannel getAnnouncementChannel() {
        return announcementChannel;
    }

    @Override
    public GuildConfiguration setAnnouncementChannel(TextChannel announcementChannel) {
        this.announcementChannel = announcementChannel;
        return this;
    }

    public TextChannel getPlayerCodesChannel() {
        return playerCodesChannel;
    }

    @Override
    public GuildConfiguration setPlayerCodesChannel(TextChannel playerCodesChannel) {
        this.playerCodesChannel = playerCodesChannel;
        return this;
    }

    public VoiceChannel getCountdownChannel() {
        return countdownChannel;
    }

    @Override
    public GuildConfiguration setCountdownChannel(VoiceChannel countdownChannel) {
        this.countdownChannel = countdownChannel;
        return this;
    }

    public int getCountdownTimeout() {
        return countdownTimeout;
    }

    @Override
    public GuildConfiguration setCountdownTimeout(int countdownTimeout) {
        this.countdownTimeout = countdownTimeout;
        return this;
    }

    /**
     * Converts this current configuration to one that can be written to a YAML file.
     *
     * @return a raw guild configuration build from this one, can be null if the guild was not setup correctly.
     */
    public RawGuildConfiguration dump() {
        if (CheckUtility.anyNull(administratorRole, announcementRole, announcementChannel, playerCodesChannel, countdownChannel)) {
            return null;
        }
        return new RawGuildConfiguration(guildId, administratorRole.getId(), announcementRole.getId(), announcementChannel.getId(),
                playerCodesChannel.getId(),
                countdownChannel.getId(), countdownTimeout);
    }

}
