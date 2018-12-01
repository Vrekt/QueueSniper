package me.vrekt.queuesniper.guild.setup;

import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.message.MessageEventCallable;
import me.vrekt.queuesniper.message.TemporaryMessageEvent;
import me.vrekt.queuesniper.permission.PermissionChecker;
import me.vrekt.queuesniper.result.MessageActionHandler;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class GuildSetupConfiguration implements MessageEventCallable {

    private final GuildConfiguration configuration;

    private SetupStep step;
    private boolean setup;

    private TemporaryMessageEvent messageEvent;

    public GuildSetupConfiguration(boolean setup, GuildConfiguration configuration) {
        this.setup = setup;
        this.configuration = configuration;
    }

    public boolean isSetup() {
        return setup;
    }

    public void setSetup(boolean setup) {
        this.setup = setup;
    }

    /**
     * Registers a new {@link me.vrekt.queuesniper.message.TemporaryMessageEvent}
     * with JDA to listen to {@link net.dv8tion.jda.core.events.message.MessageReceivedEvent}
     *
     * @param jda jda
     */
    public void register(JDA jda, String guildId, TextChannel sentIn) {
        if (messageEvent != null) {
            return;
        }
        messageEvent = new TemporaryMessageEvent(this, guildId, sentIn.getId());
        jda.addEventListener(messageEvent);

        step = SetupStep.CONTROL;
        MessageActionHandler.sendMessageToChannel(sentIn, step.getDescription());
    }

    /**
     * Removes the {@link me.vrekt.queuesniper.message.TemporaryMessageEvent} event from JDA
     *
     * @param jda jda
     */
    private void deregister(JDA jda) {
        jda.removeEventListener(messageEvent);
        messageEvent = null;
    }

    @Override
    public void messageReceived(TextChannel sentIn, Message message) {
        Member member = message.getMember();

        // return if the message was sent by the bot
        if (member == configuration.getSelf()) {
            return;
        }

        String input = message.getContentDisplay();
        if (PermissionChecker.canControl(member, null)) {
            SetupStep.Response response = step.parse(input, member.getGuild(), configuration);
            switch (response) {
                case SUCCESS:
                    int index = step.getIndex() + 1;
                    if (index >= SetupStep.values().length) {
                        // finished with the setup
                        step = null;
                        MessageActionHandler.sendMessageToChannel(sentIn, "QueueSniper is now ready for use! Refer to .help if you need " +
                                "help!");
                        deregister(sentIn.getJDA());

                        setup = true;
                    } else {
                        step = SetupStep.values()[index];
                        MessageActionHandler.sendMessageToChannel(sentIn, step.getDescription());
                    }
                    break;
                case ROLE_NOT_FOUND:
                    MessageActionHandler.sendMessageToChannel(sentIn, "The role " + input + " was not found!");
                    break;
                case TEXT_CHANNEL_NOT_FOUND:
                    MessageActionHandler.sendMessageToChannel(sentIn, "The text channel " + input + " was not found!");
                    break;
                case VOICE_CHANNEL_NOT_FOUND:
                    MessageActionHandler.sendMessageToChannel(sentIn, "The voice channel " + input + " was not found!");
                    break;
                case INTEGER_PARSE_ERROR:
                    MessageActionHandler.sendMessageToChannel(sentIn, input + " is not a valid number!");
                    break;
            }
        }
    }

}
