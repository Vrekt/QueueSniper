package me.vrekt.queuesniper;

import me.vrekt.queuesniper.guild.GuildConfiguration;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class QSLogger {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final ZoneId ZONE_ID = TimeZone.getTimeZone("CST").toZoneId();

    public static void log(GuildConfiguration configuration, String info) {
        if (configuration == null) {
            System.out.println("[Core] [" + ZonedDateTime.now(ZONE_ID).format(FORMATTER) + "] " + info);
        } else {
            System.out.println("[" + configuration.getGuildId() + ":{" + configuration.getGuild().getName() + "}] [" + ZonedDateTime.now(ZONE_ID).
                    format(FORMATTER) + "] " + info);
        }
    }

}
