package me.vrekt.queuesniper.match.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import me.vrekt.queuesniper.QSEntry;
import me.vrekt.queuesniper.QSLogger;
import me.vrekt.queuesniper.match.voice.lavaplayer.LavaPlayerSendHandler;
import me.vrekt.queuesniper.permission.PermissionChecker;
import net.dv8tion.jda.core.audio.SpeakingMode;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

public class VoiceCountdownHandler {

    private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    public VoiceCountdownHandler() {
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public void countdown(Guild guild, VoiceChannel channel) {
        Member self = guild.getSelfMember();
        if (!PermissionChecker.hasVoicePermissions(channel, self)) {
            return;
        }

        final AudioPlayer player = playerManager.createPlayer();
        LavaPlayerSendHandler sendHandler = new LavaPlayerSendHandler(player);

        final AudioManager manager = guild.getAudioManager();
        player.addListener(new AudioEventAdapter() {
            @Override
            public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
                manager.closeAudioConnection();
                player.destroy();
            }
        });
        player.setVolume(50);

        playerManager.loadItem(QSEntry.getConfiguration().getValue("countdown_audio"), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                manager.setSpeakingMode(SpeakingMode.PRIORITY);
                manager.setSendingHandler(sendHandler);
                manager.openAudioConnection(channel);

                player.playTrack(track);
                QSLogger.log("Playing audio for a total of " + channel.getMembers().size() + " players.");
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {

            }

            @Override
            public void noMatches() {
                QSLogger.log("Could not find countdown audio!");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                exception.printStackTrace();
            }
        });

    }

}
