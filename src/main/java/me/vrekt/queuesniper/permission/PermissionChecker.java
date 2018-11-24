package me.vrekt.queuesniper.permission;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class PermissionChecker {

    private static final Permission[] TEXT_PERMISSIONS = new Permission[]{Permission.MESSAGE_WRITE, Permission.MESSAGE_READ,
            Permission.MESSAGE_MANAGE
            , Permission.MESSAGE_HISTORY, Permission.MESSAGE_EMBED_LINKS};

    private static final Permission[] VOICE_PERMISSIONS = new Permission[]{Permission.VOICE_CONNECT, Permission.VOICE_SPEAK,
            Permission.PRIORITY_SPEAKER};

    private static final Permission[] GENERAL_PERMISSIONS = new Permission[]{Permission.MANAGE_CHANNEL};

    public static boolean canControl(Member member, Role control) {
        return member.hasPermission(Permission.ADMINISTRATOR) || (control != null && member.getRoles().contains(control));
    }

    public static boolean hasTextPermissions(TextChannel channel, Member self) {
        return PermissionUtil.checkPermission(channel, self, TEXT_PERMISSIONS);
    }

    public static boolean hasVoicePermissions(VoiceChannel channel, Member self) {
        return PermissionUtil.checkPermission(channel, self, VOICE_PERMISSIONS);
    }

    public static boolean hasGeneralPermissions(Channel channel, Member self) {
        return PermissionUtil.checkPermission(channel, self, GENERAL_PERMISSIONS);
    }
}
