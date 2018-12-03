package me.vrekt.queuesniper.match;

import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.message.MessageEventCallable;
import me.vrekt.queuesniper.message.TemporaryMessageEvent;
import me.vrekt.queuesniper.permission.PermissionChecker;
import me.vrekt.queuesniper.result.ChannelActionHandler;
import me.vrekt.queuesniper.result.MessageActionHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to collect server IDs
 */
public class GuildMatchMonitor implements MessageEventCallable {

    // the time to wait after registering this collector before cleaning up
    private final long waitTime = 85000;

    private final GuildConfiguration configuration;

    private final Map<String, List<String>> servers = new HashMap<>();
    private final List<String> players = new ArrayList<>();

    private final EmbedBuilder serverCodes = new EmbedBuilder();
    private final TextChannel mainChannel, codesChannel;
    private final long now = System.currentTimeMillis();
    private final TemporaryMessageEvent event;

    private String serverCodesMessageId;
    private boolean finished = false;
    private int fancy = 1;

    GuildMatchMonitor(GuildConfiguration configuration, JDA jda) {
        this.configuration = configuration;

        mainChannel = configuration.getAnnouncementChannel();
        codesChannel = configuration.getCodesChannel();

        event = new TemporaryMessageEvent(this, configuration.getGuildId(), codesChannel.getId());
        jda.addEventListener(event);

        EmbedBuilder matchInstructions = new EmbedBuilder();
        matchInstructions.setColor(new Color(233, 128, 116)).addField("Waiting for server IDs...", "- Please post the last 3 characters of " +
                "your server ID in channel '#" + codesChannel.getName() + "'. This can be found in the top left of your screen.", false);

        // queue the messages and handle the failure/success
        MessageActionHandler.sendMessageToChannel(mainChannel, matchInstructions.build()).handleFailure(message -> finished = true);
        if (finished) {
            return;
        }
        matchInstructions.clear().setColor(new Color(30, 130, 89)).setAuthor("Current players and servers: ");

        // futures are inconsistent
        try {
            mainChannel.sendMessage(matchInstructions.build()).queue(message -> serverCodesMessageId = message.getId());
        } catch (InsufficientPermissionException exception) {
            finished = true;
        }

        // unlock the channel
        ChannelActionHandler.unlockChannel(codesChannel, configuration.getSelf(), configuration.getPublicRole(),
                configuration.getAnnouncementRole());
    }

    /**
     * @return true if this collector is finished collecting IDs.
     * If an exception occurs (like not being able to send messages) then this will also return true without waiting the 85000
     * milliseconds.
     */
    boolean isFinished() {
        return finished || System.currentTimeMillis() - now >= waitTime;
    }

    @Override
    public void messageReceived(TextChannel sentIn, Message message) {

        Member member = message.getMember();
        String player = member.getAsMention();
        String serverId = message.getContentDisplay().toLowerCase();

        if (serverId.length() == 3) {
            if (!servers.containsKey(serverId) && !players.contains(player)) {
                List<String> serverPlayers = new ArrayList<>();
                serverPlayers.add(player);

                servers.put(serverId, serverPlayers);
                players.add(player);
                addServerId();
            } else if (servers.containsKey(serverId) && !players.contains(player)) {
                servers.get(serverId).add(player);
                players.add(player);
                addServerId();
            }
        }
        // make sure we don't delete messages that are from us (the bot)
        if (!member.getUser().getId().equals(configuration.getSelf().getUser().getId())) {
            // dont delete commands

            if (PermissionChecker.canControl(member, configuration.getControlRole())) {
                if (serverId.startsWith(".")) {
                    return;
                }
            }

            MessageActionHandler.deleteMessageFromChannel(sentIn, message.getId());
        }
    }

    /**
     * Adds the collected server id to the embed
     */
    private void addServerId() {
        serverCodes.clear().setColor(new Color(30, 130, 89)).setAuthor("Current players and servers: ");
        fancy = 1;

        servers.forEach((serverId, players) -> {
            fancy++;
            serverCodes.addField("ID: " + serverId + " (" + players.size() + " players)", String.join(System.lineSeparator(), players), true);
            if (fancy % 2 == 0) {
                // make the embed look nice
                serverCodes.addBlankField(true);
            }
        });
        MessageActionHandler.editMessageFromChannel(mainChannel, serverCodesMessageId, serverCodes.build());
    }

    /**
     * Locks the channel and finalizes the embed + clears maps/lists.
     */
    void cleanup(JDA jda) {

        jda.removeEventListener(event);
        ChannelActionHandler.lockChannel(codesChannel, configuration.getSelf(), configuration.getPublicRole(),
                configuration.getAnnouncementRole());

        serverCodes.addField("", "Match started: (" + players.size() + " players) (" + servers.keySet().size() + " servers)", false);
        MessageActionHandler.sendMessageToChannel(codesChannel, "*Chat locked...*  Match started, good luck and have fun!");
        MessageActionHandler.editMessageFromChannel(mainChannel, serverCodesMessageId, serverCodes.build());

        servers.clear();
        players.clear();
    }

}
