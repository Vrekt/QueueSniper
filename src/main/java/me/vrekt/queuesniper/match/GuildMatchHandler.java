package me.vrekt.queuesniper.match;

import me.vrekt.queuesniper.QSLogger;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.voice.VoiceCountdownHandler;
import net.dv8tion.jda.core.JDA;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class handles starting matches and keeping track of them
 */
public class GuildMatchHandler {

    private final ConcurrentHashMap<GuildConfiguration, List<Long>> queuedMatches = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<GuildConfiguration, List<GuildMatchMonitor>> startedMatches = new ConcurrentHashMap<>();

    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private final VoiceCountdownHandler voiceCountdownHandler;
    private final JDA jda;

    public GuildMatchHandler(JDA jda, VoiceCountdownHandler voiceCountdownHandler) {
        this.jda = jda;
        this.voiceCountdownHandler = voiceCountdownHandler;
        service.scheduleWithFixedDelay(this::monitorMatches, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * Queue a match
     *
     * @param configuration the guild that it was sent from
     */
    public void queueMatch(GuildConfiguration configuration) {
        QSLogger.log(configuration, "Queued");
        if (!queuedMatches.containsKey(configuration)) {
            queuedMatches.put(configuration, new ArrayList<>());
        }
        queuedMatches.get(configuration).add(System.currentTimeMillis());
    }

    /**
     * Starts a match
     *
     * @param configuration the guild that it was sent from
     */
    private void startMatch(GuildConfiguration configuration) {
        if (!startedMatches.containsKey(configuration)) {
            startedMatches.put(configuration, new ArrayList<>());
        }

        GuildMatchMonitor monitor = new GuildMatchMonitor(configuration, jda);
        startedMatches.get(configuration).add(monitor);
        voiceCountdownHandler.countdown(configuration.getGuild(), configuration.getCountdownChannel());
    }

    /**
     * Monitor queued and started matches to remove event listeners/cleanup.
     */
    private void monitorMatches() {
        // first process the queued matches
        try {
            for (Iterator<GuildConfiguration> queuedConfigurations = queuedMatches.keySet().iterator(); queuedConfigurations.hasNext(); ) {
                GuildConfiguration configuration = queuedConfigurations.next();
                List<Long> time = queuedMatches.get(configuration);
                for (Iterator<Long> times = time.iterator(); times.hasNext(); ) {
                    // check if the match is ready to start
                    long elapsed = System.currentTimeMillis() - times.next();
                    if (elapsed >= configuration.getCountdownTimeout() * 1000) {
                        startMatch(configuration);
                        times.remove();

                        // remove this guild configuration if there was only 1 match
                        if (time.size() <= 1) {
                            queuedConfigurations.remove();
                        }
                    }
                }
            }

            // finally process the matches that have already been started
            for (Iterator<GuildConfiguration> startedConfigurations = startedMatches.keySet().iterator(); startedConfigurations.hasNext(); ) {
                GuildConfiguration configuration = startedConfigurations.next();
                List<GuildMatchMonitor> matchList = startedMatches.get(configuration);
                for (Iterator<GuildMatchMonitor> matchIterator = matchList.iterator(); matchIterator.hasNext(); ) {
                    GuildMatchMonitor monitor = matchIterator.next();
                    if (monitor.isFinished()) {
                        monitor.cleanup(jda);

                        matchIterator.remove();
                        // remove this guild configuration if there was only 1 match
                        if (matchList.size() <= 1) {
                            startedConfigurations.remove();
                        }
                    }
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();

            // i tried to prevent any exceptions from happening, but in the rare case that exceptions do occur clear the lists
            queuedMatches.clear();
            startedMatches.clear();
        }
    }

    public void stop() {

        queuedMatches.clear();
        startedMatches.clear();

        service.shutdown();
    }

}
