package me.vrekt.queuesniper.database;

import me.vrekt.queuesniper.QSLogger;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.guild.GuildConfigurationFactory;
import me.vrekt.queuesniper.guild.dump.DumpableGuildConfiguration;
import me.vrekt.queuesniper.guild.register.GuildRegisterConfiguration;
import me.vrekt.queuesniper.utility.CheckUtility;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DatabaseManager {

    private final ExecutorService service = Executors.newCachedThreadPool();

    public boolean load(JDA jda) {
        Future<Boolean> result = service.submit(() -> loadAsync(jda));

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException exception) {
            return false;
        }
    }

    private boolean loadAsync(JDA jda) {
        File file = new File("database.yaml");
        if (!file.exists()) {
            QSLogger.log("Database file not found!");
            return false;
        }

        try {
            InputStream stream = new FileInputStream(file);
            Yaml yaml = new Yaml();

            Map<String, Object> data = yaml.load(stream);

            if (data == null || data.isEmpty()) {
                return true;
            }

            for (String guildId : data.keySet()) {
                Object obj = data.get(guildId);
                if (obj instanceof DumpableGuildConfiguration) {
                    DumpableGuildConfiguration configurable = (DumpableGuildConfiguration) obj;

                    Guild guild = jda.getGuildById(guildId);
                    if (guild == null) {
                        QSLogger.log("Could not load guild: " + guildId + ", this guild is no longer using QueueSniper.");
                        continue;
                    }

                    GuildConfiguration configuration = new GuildConfiguration(guildId, guild, guild.getSelfMember(), guild.getPublicRole());

                    // verify the guilds setup is still valid
                    Role controlRole = guild.getRoleById(configurable.controlRoleId);
                    Role announcementRole = guild.getRoleById(configurable.announcementRoleId);

                    TextChannel announcementChannel = guild.getTextChannelById(configurable.announcementChannelId);
                    TextChannel codesChannel = guild.getTextChannelById(configurable.codesChannelId);

                    VoiceChannel countdownChannel = guild.getVoiceChannelById(configurable.countdownChannelId);
                    int timeout = configurable.timeout;
                    if (CheckUtility.anyNull(controlRole, announcementRole, announcementChannel, codesChannel,
                            countdownChannel)) {
                        QSLogger.log("Guild: " + guildId + ":" + guild.getName() + " will need to be setup again.");
                        GuildConfigurationFactory.add(configuration);
                        continue;
                    }

                    configuration.setControlRole(controlRole).setAnnouncementRole(announcementRole).setAnnouncementChannel(announcementChannel).
                            setCodesChannel(codesChannel).setCountdownChannel(countdownChannel).setTimeout(timeout);
                    configuration.setRegisterConfiguration(new GuildRegisterConfiguration(true));

                    GuildConfigurationFactory.add(configuration);
                    QSLogger.log(configuration, "Finished loading guild!");
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return true;
    }

    public boolean save(File file) {
        Future<Boolean> result = service.submit(() -> saveAsync(file));
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException exception) {
            return false;
        }
    }

    private boolean saveAsync(File file) {
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    return false;
                }
            } catch (IOException exception) {
                return false;
            }
            return false;
        }

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        Yaml yaml = new Yaml(options);
        try {
            final Map<String, GuildConfiguration> data = GuildConfigurationFactory.getConfigurationMap();

            if (data.isEmpty()) {
                return true;
            }

            FileWriter writer = new FileWriter(file);
            final Map<String, DumpableGuildConfiguration> dump = new HashMap<>();
            data.forEach((guildId, configuration) -> dump.put(guildId, configuration.dump()));

            yaml.dump(dump, writer);
            writer.flush();
            writer.close();

        } catch (IOException exception) {
            return false;
        }
        return true;
    }
}
