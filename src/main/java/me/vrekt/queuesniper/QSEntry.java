package me.vrekt.queuesniper;

import me.vrekt.queuesniper.database.DatabaseManager;
import me.vrekt.queuesniper.listener.CommandListener;
import me.vrekt.queuesniper.listener.GuildListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import javax.security.auth.login.LoginException;

class QSEntry {

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
        System.out.println("Attempting to save database before shutting down... please wait.");
        boolean trySave = databaseManager.save();
        if (!trySave) {
            System.out.println("Database could not save! Attempting to dump to another file.");
            databaseManager.attemptSave();
        }
    }

    private void register(String token) {
        try {
            new JDABuilder(AccountType.BOT).setToken(token).setEventManager(new AnnotatedEventManager()).addEventListener(this).build();
        } catch (LoginException exception) {
            System.out.println("The token is invalid!");
        }
    }

    @SubscribeEvent
    public void onJdaReady(ReadyEvent event) {
        QSLogger.log(null, "JDA is ready! Starting...");

        JDA jda = event.getJDA();
        boolean tryLoad = databaseManager.load(jda);
        if (!tryLoad) {
            System.out.println("Could not load database. Shutting down..");
            jda.shutdownNow();
        } else {
            jda.addEventListener(new CommandListener(jda));
            jda.addEventListener(new GuildListener());
        }
    }

}
