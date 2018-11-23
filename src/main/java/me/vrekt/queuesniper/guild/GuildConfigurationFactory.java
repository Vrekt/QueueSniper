package me.vrekt.queuesniper.guild;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class GuildConfigurationFactory {
    private static final HashMap<String, GuildConfiguration> CONFIGURATION_MAP = new HashMap<>();

    /**
     * Retrieve the GuildConfiguration.
     *
     * @param id the ID of the guild.
     * @return the GuildConfiguration, a new one is created if it doesn't already exist.
     */
    @Nullable
    public static GuildConfiguration get(String id) {
        if (CONFIGURATION_MAP.containsKey(id)) {
            return CONFIGURATION_MAP.get(id);
        }
        return null;
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

}
