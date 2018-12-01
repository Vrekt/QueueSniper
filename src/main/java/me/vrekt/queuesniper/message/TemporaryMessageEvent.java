package me.vrekt.queuesniper.message;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

/**
 * This class is meant for message events that only need to be registered for a short amount of time.
 */
public class TemporaryMessageEvent {

    private final MessageEventCallable callable;
    private final String guildId, textChannel;

    public TemporaryMessageEvent(MessageEventCallable callable, String guildId, String textChannel) {
        this.callable = callable;
        this.guildId = guildId;
        this.textChannel = textChannel;
    }

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannelType() == ChannelType.TEXT) {
            TextChannel channel = event.getTextChannel();
            Guild guild = event.getGuild();

            // make sure it was sent in the correct guild
            if (guild.getId().equals(guildId) && channel.getId().equals(textChannel)) {
                callable.messageReceived(channel, event.getMessage());
            }
        }
    }

}
