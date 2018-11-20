package me.vrekt.queuesniper.match.collector;

import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.utility.PermissionUtility;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class MatchIdCollector {

    private final EmbedBuilder serverIdEmbed = new EmbedBuilder();
    private final GuildConfiguration configuration;

    private final TextChannel channel;
    private final long timeAdded;
    private final Map<String, String> serverIds = new HashMap<>();
    private int fancyTracker = 1, totalPlayers;
    private String serverEmbedId;

    public MatchIdCollector(GuildConfiguration configuration, JDA jda) {
        this.configuration = configuration;
        this.timeAdded = System.currentTimeMillis();
        jda.addEventListener(this);

        channel = configuration.getAnnouncementChannel();
        EmbedBuilder instructions = new EmbedBuilder();

        // server id instructions
        instructions.setColor(new Color(233, 128, 116)).addField("Waiting for server IDs...", " - Please post the last 3 characters" +
                        " of your Server ID. This can be found in the top left of your screen.",
                false);
        channel.sendMessage(instructions.build()).queue();

        // SERVER IDS.
        instructions.clear().setColor(new Color(30, 130, 89)).setAuthor("Current players and servers: ");
        channel.sendMessage(instructions.build()).queue(msg -> serverEmbedId = msg.getId());

        // allow users to post codes
        channel.putPermissionOverride(configuration.getGuild().getPublicRole()).setAllow(Permission.MESSAGE_WRITE).queue();
    }

    @SubscribeEvent
    public void monitorChannel(MessageReceivedEvent event) {
        if (event.getChannelType() == ChannelType.TEXT) {
            if (event.getGuild().equals(configuration.getGuild()) &&
                    event.getTextChannel().equals(configuration.getPlayerCodesChannel())) {

                Message message = event.getMessage();
                Member member = event.getMember();
                String serverId = message.getContentDisplay().toLowerCase();

                if (serverId.length() == 3) {
                    totalPlayers++;
                    if (!serverIds.containsKey(serverId)) {
                        serverIds.put(serverId, member.getAsMention());
                    } else {
                        String builder = serverIds.get(serverId) +
                                System.lineSeparator() +
                                member.getAsMention();
                        serverIds.put(serverId, builder);
                    }
                    updateServerIds();
                }

                if (!PermissionUtility.isAdministrator(member, configuration.getAdministratorRole())) {
                    event.getTextChannel().deleteMessageById(message.getId()).queue();
                }
            }
        }
    }

    /**
     * Update the embed builder with the new IDS then queue it.
     */
    private void updateServerIds() {
        serverIdEmbed.clearFields();
        serverIdEmbed.setColor(new Color(30, 130, 89)).setAuthor("Current players and servers: ");
        fancyTracker = 1;

        serverIds.forEach((serverId, players) -> {
            fancyTracker++;
            serverIdEmbed.addField("ID: " + serverId, players, true);
            if (fancyTracker % 2 == 0) {
                serverIdEmbed.addBlankField(true);
            }
        });
        channel.editMessageById(serverEmbedId, serverIdEmbed.build()).queue();
    }


    /**
     * @return if this collector is done collecting IDs.
     */
    public boolean finished() {
        return System.currentTimeMillis() - timeAdded >= 2 * 60000;
    }

    /**
     * Clean up this collector.
     *
     * @param jda jda instance.
     */
    public void cleanup(JDA jda) {
        channel.putPermissionOverride(configuration.getGuild().getPublicRole()).setAllow(Permission.MESSAGE_WRITE).queue();
        jda.removeEventListener(this);

        serverIdEmbed.addField("", "Match started: (" + totalPlayers + " players) (" + serverIds.size() + " servers)", false);
        channel.editMessageById(serverEmbedId, serverIdEmbed.build()).queue();
        channel.sendMessage("*Chat locked...* Match started, good luck and have fun!").queue();
    }

}
