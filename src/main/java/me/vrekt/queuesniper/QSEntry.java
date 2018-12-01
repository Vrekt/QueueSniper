package me.vrekt.queuesniper;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;

public class QSEntry {

    public static String WORKING_DIRECTORY;

    public static void main(String[] args) {

        try {
            CodeSource source = QSEntry.class.getProtectionDomain().getCodeSource();
            WORKING_DIRECTORY = new File(source.getLocation().toURI().getPath()).getParentFile().getPath() + "\\";
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
            return;
        }

        // java -jar Jar.jar configuration.yaml token
        if (args.length == 0) {
            System.out.println("QueueSniper must be started with the following arguments: ");
            System.out.println("<bot token> <configuration file>");
            System.out.println();
            System.out.println("NOTE: If no configuration file is specified one will be created automatically in the current directory.");
            return;
        }

        String token = args[0];
        String configurationFile;
        if (args.length > 1) {
            configurationFile = args[1];
        } else {
            configurationFile = WORKING_DIRECTORY + "configuration.yaml";
        }

        try {
            new JDABuilder(AccountType.BOT).setToken(token).setEventManager(new AnnotatedEventManager()).addEventListener(new QSRegister(configurationFile)).build();
        } catch (LoginException exception) {
            System.out.println("JDA failed to login! Your token is invalid!");
        }
    }

}
