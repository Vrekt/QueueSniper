package me.vrekt.queuesniper.configuration;

import me.vrekt.queuesniper.QSLogger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Currently there are only two values that needs to be stored, but future plans are to add more configuration options.
 */

public class BotConfiguration {

    private final Map<String, String> entries = new HashMap<>();

    public void load(String read) {
        File file = new File(read);
        if (!file.exists()) {
            writeConfig(file);
            return;
        }

        Yaml yaml = new Yaml();
        try {
            InputStream stream = new FileInputStream(file);
            Map<String, Object> data = yaml.load(stream);

            if (data == null || data.isEmpty()) {
                writeConfig(file);
                return;
            }

            for (String name : data.keySet()) {
                Object obj = data.get(name);
                if (obj instanceof String) {
                    String value = (String) obj;
                    entries.put(name, value);
                    QSLogger.log("Added configuration entry {" + name + ":" + value + "}");
                }
            }
            QSLogger.log("Finished loading configuration.");
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    private void useDefaultConfig() {
        QSLogger.log("Could not create or read configuration file, using default for now.");

        entries.put("database_file", "database.yaml");
        entries.put("countdown_audio", "countdown.mp3");
        QSLogger.log("Finished loading configuration...");
    }

    private void writeConfig(File file) {
        QSLogger.log("Creating configuration file.");
        try {
            if (file.exists()) {
                DumperOptions options = new DumperOptions();

                options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                options.setPrettyFlow(true);
                Yaml yaml = new Yaml(options);

                Map<String, String> data = new HashMap<>();
                data.put("database_file", "database.yaml");
                data.put("countdown_audio", "countdown.mp3");

                FileWriter writer = new FileWriter(file);
                yaml.dump(data, writer);

                writer.flush();
                writer.close();
                QSLogger.log("Dumped configuration to: " + file.getPath());
            }
            useDefaultConfig();
        } catch (IOException exception) {
            useDefaultConfig();
        }
    }

    public String getValue(String name) {
        return entries.getOrDefault(name, "Not found");
    }

}
