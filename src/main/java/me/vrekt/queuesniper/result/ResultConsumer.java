package me.vrekt.queuesniper.result;

import net.dv8tion.jda.core.entities.Message;

/**
 * A class used to handle a result
 */
public interface ResultConsumer {

    /**
     * Handle the result of a action
     */
    void handleResult(Message message);

}
