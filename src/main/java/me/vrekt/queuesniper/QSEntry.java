package me.vrekt.queuesniper;

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

    private final DatabaseManager databaseManager = new DatabaseManager();

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("QueueSniper must be started with a token.");
            return;
        }

        QSEntry entry = new QSEntry();
        Runtime.getRuntime().addShutdownHook(new Thread(entry::shutdown));

        String token = args[0];
        entry.register(token);
    }

    private void shutdown() {
        QSLogger.log("Attempting to save database before shutting down... please wait.");
        boolean save = databaseManager.save(new File("database.yaml"));
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
        boolean load = databaseManager.load(jda);
        if (!load) {
            QSLogger.log("Could not load database! Exiting...");
            Runtime.getRuntime().halt(0);
        } else {
            QSLogger.log("Finished reading database!");
            ListenerRegister.register(jda);
        }

    }

}
