package me.vrekt.queuesniper.listener;

import me.vrekt.queuesniper.command.CommandExecutor;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.guild.GuildConfigurationBuilder;
import me.vrekt.queuesniper.guild.GuildConfigurationFactory;
import me.vrekt.queuesniper.guild.setup.GuildSetupConfiguration;
import me.vrekt.queuesniper.match.GuildMatchHandler;
import me.vrekt.queuesniper.permission.PermissionChecker;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class CommandListener {

    private final CommandExecutor executor;

    public CommandListener(JDA jda, GuildMatchHandler matchHandler) {
        executor = new CommandExecutor(jda);
        executor.initializeCommands(matchHandler);
    }

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
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
                // make sure a guild gets registered if it wasn't already, there was a period of time
                // where there was no guild join listener
                configuration = new GuildConfigurationBuilder(guild.getId(), guild, guild.getSelfMember(), guild.getPublicRole()).build();
                configuration.setRegisterConfiguration(new GuildSetupConfiguration(false, configuration));
                GuildConfigurationFactory.add(configuration);
            }

            if (PermissionChecker.canControl(member, configuration.getControlRole())) {
                executor.executeCommand(content, member, event.getTextChannel(), configuration);
            }
        }
    }

}
