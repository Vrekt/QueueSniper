package me.vrekt.queuesniper.match.collector;

import me.vrekt.queuesniper.QSLogger;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.permission.PermissionChecker;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MatchCollector {

    private final GuildConfiguration configuration;

    private final Map<String, List<String>> servers = new HashMap<>();
    private final List<String> players = new ArrayList<>();

    private final EmbedBuilder serverCodes = new EmbedBuilder();
    private final TextChannel mainChannel, codesChannel;
    private final long now = System.currentTimeMillis();

    private String serverCodesMessageId;
    private int inline = 1;

    public MatchCollector(GuildConfiguration configuration) {
        this.configuration = configuration;

        mainChannel = configuration.getAnnouncementChannel();
        codesChannel = configuration.getPlayerCodesChannel();

        EmbedBuilder matchInstructions = new EmbedBuilder();
        matchInstructions.setColor(new Color(233, 128, 116)).addField("Waiting for server IDs...", "- Please post the last 3 characters of " +
                "your server ID in channel '#" + codesChannel.getName() + "'. This can be found in the top left of your screen.", false);
        mainChannel.sendMessage(matchInstructions.build()).queueAfter(1, TimeUnit.SECONDS);

        matchInstructions.clear().setColor(new Color(30, 130, 89)).setAuthor("Current players and servers: ");
        mainChannel.sendMessage(matchInstructions.build()).queueAfter(2, TimeUnit.SECONDS, message -> serverCodesMessageId =
                message.getId());

        // open the channel
        if (PermissionChecker.hasGeneralPermissions(codesChannel, configuration.getSelf())) {
            codesChannel.putPermissionOverride(configuration.getPublicRole()).setAllow(Permission.MESSAGE_WRITE).queue();
        }

    }

    private void post() {
        serverCodes.clear();
        serverCodes.setColor(new Color(30, 130, 89)).setAuthor("Current players and servers: ");
        inline = 1;

        servers.forEach((serverId, players) -> {
            inline++;
            serverCodes.addField("ID: " + serverId, String.join(System.lineSeparator(), players), true);
            if (inline % 2 == 0) {
                serverCodes.addBlankField(true);
            }
        });
        postEdit();
    }

    public boolean matchStarted() {
        return System.currentTimeMillis() - now >= 85000;
    }

    private void postEdit() {
        try {
            mainChannel.editMessageById(serverCodesMessageId, serverCodes.build()).queue();
        } catch (ErrorResponseException exception) {
            mainChannel.sendMessage(serverCodes.build()).queue(message -> serverCodesMessageId = message.getId());
        }
    }

    public void cleanup() {
        QSLogger.log(configuration, "Finished collecting IDs, match has been started.");

        if (PermissionChecker.hasGeneralPermissions(codesChannel, configuration.getSelf())) {
            codesChannel.putPermissionOverride(configuration.getPublicRole()).setDeny(Permission.MESSAGE_WRITE).queue();
        }

        serverCodes.addField("", "Match started: (" + players.size() + " players) (" + servers.keySet().size() + " servers)", false);
        codesChannel.sendMessage("*Chat locked...*  Match started, good luck and have fun!").queue();
        postEdit();

        servers.clear();
        players.clear();
    }

    @SubscribeEvent
    public void onCodePosted(MessageReceivedEvent event) {
        if (event.getChannelType() == ChannelType.TEXT) {

            Guild guild = event.getGuild();
            TextChannel sentIn = event.getTextChannel();
            if (guild.getId().equals(configuration.getGuildId()) && sentIn.getId().equals(codesChannel.getId())) {
                Message message = event.getMessage();
                String player = event.getMember().getAsMention();
                String serverId = message.getContentDisplay().toLowerCase();

                if (serverId.length() == 3) {
                    if (!servers.containsKey(serverId) && !players.contains(player)) {
                        List<String> serverPlayers = new ArrayList<>();
                        serverPlayers.add(player);

                        servers.put(serverId, serverPlayers);
                        players.add(player);
                        post();
                    } else if (servers.containsKey(serverId) && !players.contains(player)) {
                        servers.get(serverId).add(player);
                        players.add(player);
                        post();
                    }
                }

                if (!PermissionChecker.isAdministrator(event.getMember(), configuration.getAdministratorRole())) {
                    if (PermissionChecker.hasGeneralPermissions(sentIn, configuration.getSelf())) {
                        sentIn.deleteMessageById(message.getId()).queue();
                    }
                }
            }
        }
    }

}
