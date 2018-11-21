package me.vrekt.queuesniper.database;

import me.vrekt.queuesniper.QSLogger;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.guild.RawGuildConfiguration;
import me.vrekt.queuesniper.guild.setup.GuildSetupConfiguration;
import me.vrekt.queuesniper.utility.CheckUtility;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DatabaseManager {

    /**
     * Load the database file async.
     *
     * @param jda the JDA instance
     * @return true if reading succeeded, false otherwise.
     */
    public boolean load(final JDA jda) {
        final ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Boolean> loadOperation = service.submit(() -> loadAsync(jda));

        boolean result;
        try {
            result = loadOperation.get();
        } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
            return false;
        }
        service.shutdown();
        return result;
    }

    /**
     * Load the database file async.
     *
     * @param jda the JDA instance
     * @return true if reading succeeded, false otherwise.
     */
    private boolean loadAsync(final JDA jda) {
        File file = new File("database.yaml");
        if (!file.exists()) {
            QSLogger.log(null, "Could not find database file 'database.yaml' ensure it is in the same directory as this jar!");
            return false;
        }
        try {
            InputStream stream = new FileInputStream(file);

            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(stream);

            if (data == null || data.isEmpty()) {
                return true;
            }

            for (String id : data.keySet()) {
                Object object = data.get(id);
                if (object instanceof RawGuildConfiguration) {
                    RawGuildConfiguration rawGuildConfiguration = (RawGuildConfiguration) object;
                    Guild guild = jda.getGuildById(id);
                    if (guild == null) {
                        // no longer valid;
                        continue;
                    }

                    Member self = guild.getSelfMember();
                    Role administratorRole = guild.getRoleById(rawGuildConfiguration.getAdministratorRoleId());
                    Role announcementRole = guild.getRoleById(rawGuildConfiguration.getAnnouncementRoleId());

                    TextChannel announcementChannel = guild.getTextChannelById(rawGuildConfiguration.getAnnouncementChannelId());
                    TextChannel playerCodesChannel = guild.getTextChannelById(rawGuildConfiguration.getPlayerCodesChannelId());

                    VoiceChannel countdownChannel = guild.getVoiceChannelById(rawGuildConfiguration.getCountdownChannelId());
                    int countdownTimeout = rawGuildConfiguration.getCountdownTimeout();

                    if (CheckUtility.anyNull(self, administratorRole, announcementRole, announcementChannel, playerCodesChannel,
                            countdownChannel)) {
                        // no longer valid
                        continue;
                    }

                    GuildConfiguration.add(new GuildConfiguration(id).setGuild(guild).setSelf(self).
                            setGuildSetupConfiguration(new GuildSetupConfiguration(true)).
                            setAdministratorRole(administratorRole).
                            setAnnouncementRole(announcementRole).
                            setPublicRole(guild.getPublicRole()).
                            setAnnouncementChannel(announcementChannel).
                            setPlayerCodesChannel(playerCodesChannel).setCountdownChannel(countdownChannel)
                            .setCountdownTimeout(countdownTimeout));
                }
            }

            stream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
        QSLogger.log(null, "Finished reading database");
        return true;
    }

    /**
     * Save the database file async.
     *
     * @return true if saving succeeded, false otherwise.
     */
    public boolean save() {
        final ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Boolean> saveOperation = service.submit(() -> saveAsync(new File("database.yaml")));
        try {
            boolean result = saveOperation.get();
            service.shutdownNow();
            return result;
        } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Save the database file async.
     *
     * @return true if saving succeeded, false otherwise.
     */
    private boolean saveAsync(File file) {
        if (!file.exists()) {
            QSLogger.log(null, "Could not find database file 'database.yaml' ensure it is in the same directory as this jar!");
            return false;
        }

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        Yaml yaml = new Yaml(options);
        try {
            FileWriter writer = new FileWriter(file);
            final Map<String, RawGuildConfiguration> dump = new HashMap<>();
            final Map<String, GuildConfiguration> data = GuildConfiguration.getConfigurationMap();
            if (data.isEmpty()) {
                return true;
            }

            data.forEach((id, guildConfiguration) -> {
                RawGuildConfiguration raw = guildConfiguration.dump();
                if (raw == null) {
                    QSLogger.log(null, "Found invalid guild in configuration, ignoring for now.");
                } else {
                    dump.put(id, raw);
                }
            });

            yaml.dump(dump, writer);
            writer.flush();
            writer.close();
        } catch (IOException exception) {
            return false;
        }
        return true;
    }

    /**
     * Final attempt to save the database.
     */
    public void attemptSave() {
        final ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> saveAsync(new File("database-backup-" + System.currentTimeMillis() + ".yaml")));
    }

}
