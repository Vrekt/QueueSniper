package me.vrekt.queuesniper.database;

import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.guild.GuildConfigurationBuilder;
import me.vrekt.queuesniper.guild.GuildConfigurationFactory;
import me.vrekt.queuesniper.guild.YamlGuildConfiguration;
import me.vrekt.queuesniper.guild.setup.GuildSetupConfiguration;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DatabaseManager {

    /**
     * Loads the database async
     *
     * @param jda  jda
     * @param file the file location
     * @return false if loading failed.
     */
    public boolean load(JDA jda, String file) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Boolean> result = service.submit(() -> loadAsync(jda, file));

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Attempts to load the database.
     * This method should be called on another thread.
     *
     * @param jda          jda
     * @param fileLocation the file location
     * @return false if loading failed.
     */
    private boolean loadAsync(JDA jda, String fileLocation) {
        File file = new File(fileLocation);
        try {
            if (file.createNewFile()) {
                return true;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }

        List<String> invalidKeys = new ArrayList<>();
        try (InputStream fs = new FileInputStream(file)) {
            Yaml yaml = new Yaml();

            Map<String, Object> data = yaml.load(fs);
            if (data == null || data.isEmpty()) {
                return true;
            }

            for (String guildId : data.keySet()) {
                Object object = data.get(guildId);
                if (object instanceof YamlGuildConfiguration) {
                    YamlGuildConfiguration configurationData = (YamlGuildConfiguration) object;
                    Guild guild = jda.getGuildById(guildId);

                    if (guild == null) {
                        invalidKeys.add(guildId);
                        continue;
                    }

                    GuildConfigurationBuilder builder = new GuildConfigurationBuilder(guildId, guild, guild.getSelfMember(),
                            guild.getPublicRole());

                    // verify everything that was saved is still valid relative to the guild.
                    Role controlRole = guild.getRoleById(configurationData.controlRoleId);
                    Role announcementRole = guild.getRoleById(configurationData.announcementRoleId);

                    TextChannel announcementChannel = guild.getTextChannelById(configurationData.announcementChannelId);
                    TextChannel codesChannel = guild.getTextChannelById(configurationData.codesChannelId);

                    VoiceChannel countdownChannel = guild.getVoiceChannelById(configurationData.countdownChannelId);
                    int countdownTimeout = configurationData.countdownTimeout;

                    // if anything is null add it to the invalid list and continue
                    if (controlRole == null || announcementRole == null || announcementChannel == null || codesChannel == null || countdownChannel == null) {
                        GuildConfigurationFactory.add(builder.build());
                        invalidKeys.add(guildId);
                        continue;
                    }

                    builder.setControlRole(controlRole).setAnnouncementRole(announcementRole).setAnnouncementChannel(announcementChannel).
                            setCodesChannel(codesChannel).setCountdownChannel(countdownChannel).setCountdownTimeout(countdownTimeout);

                    // build and add
                    GuildConfiguration configuration = builder.build();
                    configuration.setRegisterConfiguration(new GuildSetupConfiguration(true, configuration));
                    GuildConfigurationFactory.add(configuration);
                }
            }

        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }

        if (!invalidKeys.isEmpty()) {
            cleanInvalidKeys(file, invalidKeys);
        }

        return true;
    }

    /**
     * Attempts to remove invalid guilds in the database file.
     *
     * @param file        the file
     * @param invalidKeys a list of invalid keys
     */
    private void cleanInvalidKeys(File file, List<String> invalidKeys) {
        Yaml yaml = new Yaml();

    }

    /**
     * Attempts to save the database async.
     *
     * @param file the file
     * @return false if saving failed.
     */
    public boolean save(String file) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Boolean> result = service.submit(() -> saveAsync(file));

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Attempts to save the database.
     * This methods should be called on another thread
     *
     * @param fileLocation the file location
     * @return false if saving failed.
     */
    private boolean saveAsync(String fileLocation) {
        File file = new File(fileLocation);
        try {
            if (file.createNewFile()) {
                System.out.println("WARNING: The database file was deleted, a new one will be created.");
                System.out.println("Created database file: " + fileLocation);
            }
        } catch (IOException exception) {
            return false;
        }

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        Yaml yaml = new Yaml(options);
        try (FileWriter fs = new FileWriter(file)) {
            Map<String, GuildConfiguration> data = GuildConfigurationFactory.getConfigurationMap();

            if (data.isEmpty()) {
                fs.close();
                return true;
            }

            Map<String, YamlGuildConfiguration> dump = new HashMap<>();
            data.forEach((key, value) -> {
                YamlGuildConfiguration check = value.dump();
                if (check == null) {
                    // continue since the guild wasn't setup
                    return;
                }
                dump.put(key, check);
            });
            yaml.dump(dump, fs);
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }

        return true;
    }

}
