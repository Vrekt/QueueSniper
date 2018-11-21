package me.vrekt.queuesniper;

import me.vrekt.queuesniper.guild.GuildConfiguration;

public class QSLogger {

    public static void log(GuildConfiguration configuration, String info) {
        if (configuration == null) {
            System.out.println("[Core] [INFO] " + info);
        } else {
            System.out.println("[" + configuration.getGuildId() + ":{" + configuration.getGuild().getName() + "} INFO: " + info);
        }
    }

}
