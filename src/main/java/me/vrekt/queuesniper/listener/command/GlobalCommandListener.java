package me.vrekt.queuesniper.listener.command;

import me.vrekt.queuesniper.QSLogger;
import me.vrekt.queuesniper.command.CommandExecutor;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.guild.GuildConfigurationFactory;
import me.vrekt.queuesniper.permission.PermissionChecker;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class GlobalCommandListener {

    private final CommandExecutor executor;

    public GlobalCommandListener(JDA jda) {
        executor = new CommandExecutor(jda);
        executor.initializeCommands();
    }

    @SubscribeEvent
    public void onMessageReceieved(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.TEXT)) {
            Message message = event.getMessage();
            String content = message.getContentDisplay();
            if (message.isWebhookMessage() || !content.startsWith(".")) {
                return;
            }

            Member member = event.getMember();
            Guild guild = event.getGuild();
            GuildConfiguration configuration = GuildConfigurationFactory.get(guild.getId());
            if (configuration == null) {
                return;
            }

            if (PermissionChecker.canControl(member, configuration.getControlRole())) {
                try {
                    executor.executeCommand(content, member, event.getTextChannel(), configuration);
                } catch (Exception exception) {
                    QSLogger.log(configuration, "Failed to execute command: " + content);
                    exception.printStackTrace();
                }
            }
        }
    }

}
