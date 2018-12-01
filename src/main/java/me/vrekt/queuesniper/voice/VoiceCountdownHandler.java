package me.vrekt.queuesniper.voice;

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
import me.vrekt.queuesniper.QSLogger;
import me.vrekt.queuesniper.configuration.Configuration;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

public class VoiceCountdownHandler {

    private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private final Configuration configuration;

    public VoiceCountdownHandler(Configuration configuration) {
        AudioSourceManagers.registerLocalSource(playerManager);
        this.configuration = configuration;
    }

    public void countdown(Guild guild, VoiceChannel channel) {
        final AudioPlayer player = playerManager.createPlayer();
        LavaPlayerSendHandler sender = new LavaPlayerSendHandler(player);
        final AudioManager manager = guild.getAudioManager();

        player.addListener(new AudioEventAdapter() {
            @Override
            public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
                manager.closeAudioConnection();
                manager.setSendingHandler(null);
                player.destroy();
            }
        });

        playerManager.loadItem(configuration.getValue("countdown_audio_location"), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                manager.setSendingHandler(sender);
                manager.openAudioConnection(channel);

                int volume = 50;
                try {
                    volume = Integer.parseInt(configuration.getValue("countdown_audio_volume"));
                } catch (NumberFormatException exception) {
                    QSLogger.log("The value 'countdown_audio_volume' in the configuration file is an invalid number! ");
                }

                player.setVolume(volume);
                player.playTrack(track);
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
