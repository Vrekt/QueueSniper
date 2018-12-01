package me.vrekt.queuesniper.result;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.requests.RequestFuture;

public class MessageActionHandler {

    /**
     * Send a message to the text channel
     *
     * @param channel the channel
     * @param message the message
     * @return {@link MessageActionHandler} which specifies if the operation failed.
     */
    public static ActionResult sendMessageToChannel(TextChannel channel, String message) {
        if (channel == null || message == null) {
            return new ActionResult(true);
        }

        ActionResult result = new ActionResult(false);
        try {
            channel.sendMessage(message).queue(result::setMessage);
        } catch (ErrorResponseException | InsufficientPermissionException exception) {
            return new ActionResult(true);
        }
        return result;
    }

    /**
     * Send a message to the text channel.
     * This method uses {@link net.dv8tion.jda.core.requests.RequestFuture} to grab the message instead of waiting for queue();
     *
     * @param channel the channel
     * @param message the message
     * @return {@link MessageActionHandler} which specifies if the operation failed.
     */
    public static ActionResult sendMessageToChannel(TextChannel channel, MessageEmbed message) {
        if (channel == null || message == null) {
            return new ActionResult(true);
        }

        ActionResult result = new ActionResult(false);
        try {
            RequestFuture<Message> future = channel.sendMessage(message).submit(false);
            result.setMessage(future.getNow(null));
        } catch (ErrorResponseException | InsufficientPermissionException exception) {
            return new ActionResult(true);
        }
        return result;
    }

    /**
     * Send multiple messages to a text channel
     *
     * @param channel  the channel
     * @param messages the messages
     * @return {@link MessageActionHandler} which specifies if the operation failed.
     */
    public static ActionResult sendMessagesToChannel(TextChannel channel, MessageEmbed... messages) {
        if (channel == null || messages == null) {
            return new ActionResult(true);
        }

        for (MessageEmbed message : messages) {
            ActionResult result = sendMessageToChannel(channel, message);
            if (result.failed()) {
                return new ActionResult(true);
            }
        }
        return new ActionResult(false);
    }

    /**
     * Edits a message in the channel
     *
     * @param channel    the channel
     * @param messageId  the message ID to edit
     * @param newMessage the new content
     * @return {@link MessageActionHandler} which specifies if the operation failed.
     */
    public static ActionResult editMessageFromChannel(TextChannel channel, String messageId, MessageEmbed newMessage) {
        if (channel == null || messageId == null || newMessage == null) {
            return new ActionResult(true);
        }

        ActionResult result = new ActionResult(false);
        try {
            channel.editMessageById(messageId, newMessage).queue(result::setMessage);
        } catch (ErrorResponseException | InsufficientPermissionException exception) {
            return new ActionResult(true);
        }
        return result;
    }

    /**
     * Delete a message from a channel
     *
     * @param channel   the channel
     * @param messageId the id of the message
     * @return {@link MessageActionHandler} which specifies if the operation failed.
     */
    public static ActionResult deleteMessageFromChannel(TextChannel channel, String messageId) {
        if (channel == null || messageId == null) {
            return new ActionResult(true);
        }

        ActionResult result = new ActionResult(false);
        try {
            channel.deleteMessageById(messageId).queue();
        } catch (ErrorResponseException | InsufficientPermissionException exception) {
            return new ActionResult(true);
        }
        return result;
    }

}
