package me.vrekt.queuesniper.utility;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

public class PermissionUtility {

    public static boolean isAdministrator(Member member, Role administratorRole) {
        return member.hasPermission(Permission.ADMINISTRATOR) || (administratorRole != null && member.getRoles().contains(administratorRole));
    }

}
