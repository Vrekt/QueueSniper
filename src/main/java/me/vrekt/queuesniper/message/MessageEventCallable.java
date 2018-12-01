package me.vrekt.queuesniper.message;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public interface MessageEventCallable {

    /**
     * Callback method for retrieving what was sent in the text channel.
     *
     * @param sentIn  the text channel where the message was sent in
     * @param message the message that was sent
     */
    void messageReceived(TextChannel sentIn, Message message);

}
