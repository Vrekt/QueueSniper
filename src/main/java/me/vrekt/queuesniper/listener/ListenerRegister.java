package me.vrekt.queuesniper.listener;

import me.vrekt.queuesniper.QSLogger;
import me.vrekt.queuesniper.listener.command.GlobalCommandListener;
import me.vrekt.queuesniper.listener.guild.GuildListener;
import net.dv8tion.jda.core.JDA;

public class ListenerRegister {

    public static void register(JDA jda) {
        QSLogger.log("Registering the following listeners: {GuildListener, GlobalCommandListener}");

        jda.addEventListener(new GuildListener());
        jda.addEventListener(new GlobalCommandListener(jda));
    }

}
