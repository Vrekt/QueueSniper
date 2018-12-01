package me.vrekt.queuesniper.configuration;

import me.vrekt.queuesniper.QSEntry;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Configuration {

    private final Map<String, String> configurationEntries = new HashMap<>();

    /**
     * Loads the configuration from the file location.
     *
     * @param fileLocation the file location, if it does not exist it will be created.
     */
    public void load(String fileLocation) {
        System.out.println("Loading configuration from: " + fileLocation);
        File file = new File(fileLocation);
        try {
            if (file.createNewFile()) {
                writeToFile(file);
                useDefault();
                return;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            useDefault();
            return;
        }

        Yaml yaml = new Yaml();
        try {
            InputStream fs = new FileInputStream(file);
            Map<String, Object> data = yaml.load(fs);

            if (data == null || data.isEmpty()) {
                writeToFile(file);
                useDefault();
                return;
            }

            // cast the object to a string and add the value.
            data.forEach((key, value) -> add(key, (String) value));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        migrateIfNeeded(3, file);
    }

    /**
     * Add a key and value to the configuration entries.
     * This method also logs what is being added.
     *
     * @param key   the key
     * @param value the value
     */
    private void add(String key, String value) {
        System.out.println("Adding configuration value (key: " + key + ", value: " + value + ")");
        configurationEntries.put(key, value);
    }

    /**
     * Writes the default configuration to the file.
     *
     * @param file the file.
     */
    private void writeToFile(File file) {
        System.out.println("Created configuration file in directory: " + file.getAbsolutePath());
        System.out.println("The default configuration will be written to this file.");

        // set options for pretty flow and block
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        // the data
        Yaml yaml = new Yaml(options);
        Map<String, String> data = new HashMap<>();
        data.put("database_file_location", QSEntry.WORKING_DIRECTORY + "database.yaml");
        data.put("countdown_audio_location", QSEntry.WORKING_DIRECTORY + "countdown.mp3");
        data.put("countdown_audio_volume", "50");

        try {
            FileWriter writer = new FileWriter(file, false);
            yaml.dump(data, writer);

            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println("Could not write configuration! Using the default configuration for now!");
        }
    }

    /**
     * Writes the default configuration to the map.
     */
    private void useDefault() {
        add("database_file_location", QSEntry.WORKING_DIRECTORY + "database.yaml");
        add("countdown_audio_location", QSEntry.WORKING_DIRECTORY + "countdown.mp3");
        add("countdown_audio_volume", "50");
    }

    /**
     * Migrates the configuration file, usually adding missed values.
     *
     * @param newSize the size the entry map should be
     * @param file    the file to save to
     */
    private void migrateIfNeeded(int newSize, File file) {
        if (configurationEntries.size() < newSize) {
            useDefault();
            writeToFile(file);
        }
    }

    /**
     * Get a value by its key name
     *
     * @param name the name
     * @return the value, returns "" if nothing was found.
     */
    public String getValue(String name) {
        return configurationEntries.getOrDefault(name, "");
    }

}
