package me.vrekt.queuesniper.guild.register;

import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.guild.GuildConfigurationFactory;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class GuildRegistrationWatcher {

    private final GuildRegisterConfiguration registerConfiguration;
    private final TextChannel monitor;
    private final Member control;

    public GuildRegistrationWatcher(TextChannel monitor, Member control, GuildRegisterConfiguration configuration) {
        this.registerConfiguration = configuration;
        this.monitor = monitor;
        this.control = control;
    }

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannelType() == ChannelType.TEXT) {
            TextChannel channel = event.getTextChannel();
            Member member = event.getMember();
            if (channel.equals(monitor) && member.equals(control)) {
                Message message = event.getMessage();
                Guild guild = event.getGuild();

                GuildConfiguration configuration = GuildConfigurationFactory.get(guild.getId());
                channel.sendMessage(registerConfiguration.checkAndReturnOutput(message.getContentDisplay(), guild, configuration)).queue();
            }
        }
    }

    public void removeListener() {
        monitor.getJDA().removeEventListener(this);
    }

    public void addListener() {
        monitor.getJDA().addEventListener(this);
    }

}
