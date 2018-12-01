package me.vrekt.queuesniper.permission;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

public class PermissionChecker {

    /**
     * @param member  the member
     * @param control the role used to control the bot.
     * @return false if the member cannot control the bot.
     */
    public static boolean canControl(Member member, Role control) {
        return member.hasPermission(Permission.ADMINISTRATOR) || (control != null && member.getRoles().contains(control));
    }
}
