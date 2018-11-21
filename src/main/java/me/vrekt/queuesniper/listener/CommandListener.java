package me.vrekt.queuesniper.listener;

import me.vrekt.queuesniper.command.CommandExecutor;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.permission.PermissionChecker;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class CommandListener {

    private final CommandExecutor commandExecutor;

    public CommandListener(JDA jda) {
        commandExecutor = new CommandExecutor(jda);
        commandExecutor.initializeCommands();
    }

    @SubscribeEvent
    public void onCommand(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.TEXT)) {
            Message message = event.getMessage();
            String content = message.getContentDisplay();
            if (message.isWebhookMessage() || !content.startsWith(".")) {
                return;
            }

            Member member = event.getMember();
            Guild guild = event.getGuild();
            GuildConfiguration configuration = GuildConfiguration.getFromId(guild.getId());
            if (PermissionChecker.isAdministrator(member, configuration.getAdministratorRole())) {
                commandExecutor.executeCommand(content, member, event.getTextChannel(), configuration);
            }
        }
    }

}
