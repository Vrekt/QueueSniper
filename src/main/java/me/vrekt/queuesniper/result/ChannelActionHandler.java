package me.vrekt.queuesniper.result;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

public class ChannelActionHandler {

    /**
     * Locks a channel so the given roles cannot message in it
     *
     * @param channel the channel
     * @param member  the self member
     * @param roles   the roles to deny
     * @return {@link MessageActionHandler} which specifies if the operation failed.
     */
    public static ActionResult lockChannel(TextChannel channel, Member member, Role... roles) {
        if (channel == null || member == null || roles == null) {
            return new ActionResult(true);
        }

        for (Role role : roles) {
            if (role == null) {
                return new ActionResult(true);
            }
            try {
                channel.putPermissionOverride(role).setDeny(Permission.MESSAGE_WRITE).queue();
            } catch (InsufficientPermissionException exception) {
                return new ActionResult(true);
            }
        }
        return new ActionResult(false);
    }

    /**
     * Unlocks a channel so the given roles can write messages
     *
     * @param channel the channel
     * @param member  the self member
     * @param roles   the roles to deny
     * @return {@link MessageActionHandler} which specifies if the operation failed.
     */
    public static ActionResult unlockChannel(TextChannel channel, Member member, Role... roles) {
        if (channel == null || member == null || roles == null) {
            return new ActionResult(true);
        }

        for (Role role : roles) {
            if (role == null) {
                return new ActionResult(true);
            }
            try {
                channel.putPermissionOverride(role).setAllow(Permission.MESSAGE_WRITE).queue();
            } catch (InsufficientPermissionException exception) {
                return new ActionResult(true);
            }
        }
        return new ActionResult(false);
    }

}
