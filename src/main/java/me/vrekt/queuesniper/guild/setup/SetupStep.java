package me.vrekt.queuesniper.guild.setup;

import me.vrekt.queuesniper.guild.GuildConfiguration;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.List;

public enum SetupStep {

    CONTROL(0, Type.ROLE, "Which role will be used to control QueueSniper?"),
    ANNOUNCEMENT_ROLE(1, Type.ROLE, "Which role will be mentioned when snipe matches and announcements are posted?"),
    ANNOUNCEMENT_CHANNEL(2, Type.TEXT_CHANNEL, "Which text channel will be used to post announcements, snipe matches and server IDs?"),
    CODES_CHANNEL(3, Type.TEXT_CHANNEL, "Which channel will players use to type their server IDs?"),
    COUNTDOWN_CHANNEL(4, Type.VOICE_CHANNEL, "Which voice channel will be used to play the countdown in?"),
    COUNTDOWN_TIMEOUT(5, Type.NONE, "Once a start command is executed how long should QueueSniper wait before counting down in " +
            "the voice channel?");

    private final int index;
    private final Type type;
    private final String description;

    SetupStep(int index, Type type, String description) {
        this.index = index;
        this.type = type;
        this.description = description;
    }

    /**
     * Parses the input sent from a user
     *
     * @param input         the input sent
     * @param guild         the guild that it was sent in
     * @param configuration the configuration of the guild
     * @return Response.ROLE_NOT_FOUND if the role was not found, Response.TEXT_CHANNEL_NOT_FOUND if the text channel was not found,
     * Response.VOICE_CHANNEL_NOT_FOUND if the voice channel was not found, Response.INTEGER_PARSE_ERROR if the number was below 0 or
     * couldn't be parsed, Response.SUCCESS if whatever operation is being is successful
     */
    public Response parse(String input, Guild guild, GuildConfiguration configuration) {
        // parse a role
        if (type == Type.ROLE) {
            List<Role> roles = guild.getRolesByName(input, false);
            if (roles.isEmpty()) return Response.ROLE_NOT_FOUND; // return if it wasn't found

            if (index == 0) {
                configuration.setControlRole(roles.get(0));
            } else {
                configuration.setAnnouncementRole(roles.get(0));
            }

            return Response.SUCCESS;
        }

        // parse a text channel
        if (type == Type.TEXT_CHANNEL) {
            List<TextChannel> channels = guild.getTextChannelsByName(input, false);
            if (channels.isEmpty()) return Response.TEXT_CHANNEL_NOT_FOUND; // return if it wasn't found

            if (index == 2) {
                configuration.setAnnouncementChannel(channels.get(0));
            } else {
                configuration.setCodesChannel(channels.get(0));
            }

            return Response.SUCCESS;
        }

        // parse a voice channel
        if (type == Type.VOICE_CHANNEL) {
            List<VoiceChannel> channels = guild.getVoiceChannelsByName(input, false);
            if (channels.isEmpty()) return Response.VOICE_CHANNEL_NOT_FOUND; // return if it wasn't found
            configuration.setCountdownChannel(channels.get(0));

            return Response.SUCCESS;
        }

        // different input that isn't a role or channel (usually a number)
        if (type == Type.NONE) {
            try {
                int countdownTimeout = Integer.parseInt(input);
                if (countdownTimeout > 0) {
                    // valid range
                    configuration.setCountdownTimeout(countdownTimeout);
                    return Response.SUCCESS;
                }
                // invalid!
                return Response.INTEGER_PARSE_ERROR;
            } catch (NumberFormatException exception) {
                return Response.INTEGER_PARSE_ERROR;
            }
        }

        return Response.SUCCESS;
    }

    public String getDescription() {
        return description;
    }

    public int getIndex() {
        return index;
    }

    enum Type {
        ROLE, TEXT_CHANNEL, VOICE_CHANNEL, NONE
    }

    enum Response {
        SUCCESS, ROLE_NOT_FOUND, TEXT_CHANNEL_NOT_FOUND, VOICE_CHANNEL_NOT_FOUND, INTEGER_PARSE_ERROR
    }

}
