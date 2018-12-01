package me.vrekt.queuesniper;

import me.vrekt.queuesniper.guild.GuildConfiguration;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class QSLogger {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss a");
    private static final ZoneId ZONE_ID = TimeZone.getTimeZone("CST").toZoneId();

    public static void log(String info) {
        System.out.println("[" + now() + "] [INFO]: " + info);
    }

    public static void log(GuildConfiguration configuration, String info) {
        System.out.println("[" + now() + "] [" + configuration.getGuildId() + ":{" + configuration.getGuild().getName() + "}] " + info);
    }

    private static String now() {
        return ZonedDateTime.now(ZONE_ID).format(FORMATTER);
    }

}
