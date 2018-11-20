package me.vrekt.queuesniper.guild;

import me.vrekt.queuesniper.guild.setup.GuildSetupConfiguration;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

interface IGuildConfiguration {

    GuildConfiguration setGuild(Guild guild);

    GuildConfiguration setGuildSetupConfiguration(GuildSetupConfiguration setupConfiguration);

    GuildConfiguration setAnnouncementRole(Role announcementRole);

    GuildConfiguration setAdministratorRole(Role administratorRole);

    GuildConfiguration setAnnouncementChannel(TextChannel announcementChannel);

    GuildConfiguration setPlayerCodesChannel(TextChannel playerCodesChannel);

    GuildConfiguration setCountdownChannel(VoiceChannel countdownChannel);

    GuildConfiguration setCountdownTimeout(int countdownTimeout);

}
