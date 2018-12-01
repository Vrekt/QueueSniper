package me.vrekt.queuesniper;

import me.vrekt.queuesniper.configuration.Configuration;
import me.vrekt.queuesniper.database.DatabaseManager;
import me.vrekt.queuesniper.listener.CommandListener;
import me.vrekt.queuesniper.listener.GuildListener;
import me.vrekt.queuesniper.match.GuildMatchHandler;
import me.vrekt.queuesniper.voice.VoiceCountdownHandler;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class QSRegister {

    private final String configurationFile;
    private final Configuration configuration = new Configuration();
    private final DatabaseManager databaseManager = new DatabaseManager();

    private GuildMatchHandler matchHandler;

    QSRegister(String configurationFile) {
        this.configurationFile = configurationFile;

        Runtime.getRuntime().addShutdownHook(new Thread(this::databaseSave));
    }

    @SubscribeEvent
    public void onJDAReady(ReadyEvent event) {
        JDA jda = event.getJDA();

        configuration.load(configurationFile);
        databaseLoad(jda);

        matchHandler = new GuildMatchHandler(jda, new VoiceCountdownHandler(configuration));
        jda.addEventListener(new GuildListener());
        jda.addEventListener(new CommandListener(jda, matchHandler));
    }

    /**
     * Loads the database
     *
     * @param jda jda
     */
    private void databaseLoad(JDA jda) {
        QSLogger.log("Loading database, please wait.");
        boolean result = databaseManager.load(jda, configuration.getValue("database_file_location"));
        if (!result) {
            QSLogger.log("Could not load database! Exiting.");
            jda.shutdownNow();
            Runtime.getRuntime().halt(0);
        }
        QSLogger.log("Finished loading database.");
    }

    /**
     * Saves the database.
     * This should be called on shutdown!
     */
    private void databaseSave() {
        QSLogger.log("Shutting down match thread");
        matchHandler.stop();

        QSLogger.log("Attempting to save database, please wait!");
        boolean result = databaseManager.save(configuration.getValue("database_file_location"));
        if (!result) {
            QSLogger.log("Could not save database! Attempting to save to another file. ");
            // last resort, if this fails we're fucked.
            databaseManager.save(QSEntry.WORKING_DIRECTORY + "database-save-" + System.currentTimeMillis() + ".yaml");
        }
        QSLogger.log("Saved database successfully, closing.");
    }

}
