package me.vrekt.queuesniper;

import me.vrekt.queuesniper.configuration.BotConfiguration;
import me.vrekt.queuesniper.database.DatabaseManager;
import me.vrekt.queuesniper.listener.ListenerRegister;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import javax.security.auth.login.LoginException;
import java.io.File;

public class QSEntry {

    private static final BotConfiguration configuration = new BotConfiguration();
    private final DatabaseManager databaseManager = new DatabaseManager();

    public static void main(String[] args) {
        System.out.println("NOTE: QueueSniper will automatically write the configuration file if it is not present!");
        if (args.length < 1) {
            System.out.println("QueueSniper must be started with the following arguments <token> (OPTIONAL) <configuration file>.");
            return;
        }

        if (args.length >= 2) {
            configuration.load(args[1]);
        } else {
            configuration.load("configuration.yaml");
        }

        QSEntry entry = new QSEntry();
        Runtime.getRuntime().addShutdownHook(new Thread(entry::shutdown));
        entry.register(args[0]);
    }

    public static BotConfiguration getConfiguration() {
        return configuration;
    }

    private void shutdown() {
        QSLogger.log("Attempting to save database before shutting down... please wait.");
        boolean save = databaseManager.save(new File(configuration.getValue("database_file")));
        if (!save) {
            QSLogger.log("Could not save to main database file, attempting backup.");
            databaseManager.save(new File("database-save-" + System.currentTimeMillis() + ".yaml"));
        }
    }

    private void register(String token) {
        try {
            new JDABuilder(AccountType.BOT).setToken(token).setEventManager(new AnnotatedEventManager()).addEventListener(this).build();
        } catch (LoginException exception) {
            QSLogger.log("Invalid token, could not start.");
        }
    }

    @SubscribeEvent
    public void onReady(ReadyEvent event) {
        QSLogger.log("JDA is ready. Starting QueueSniper....");

        JDA jda = event.getJDA();
        boolean load = databaseManager.load(jda, configuration.getValue("database_file"));
        if (!load) {
            QSLogger.log("Could not load database! Exiting...");
            Runtime.getRuntime().halt(0);
        } else {
            QSLogger.log("Finished reading database!");
            ListenerRegister.register(jda);
        }

    }
}
