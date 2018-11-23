package me.vrekt.queuesniper.match;

import me.vrekt.queuesniper.QSLogger;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.match.voice.VoiceCountdownHandler;
import net.dv8tion.jda.core.JDA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MatchHandler {

    private final Map<GuildConfiguration, List<Long>> queuedMatches = new HashMap<>();
    private final Map<GuildConfiguration, List<MatchCollector>> startedMatches = new HashMap<>();
    private final VoiceCountdownHandler voiceCountdownHandler = new VoiceCountdownHandler();
    private final JDA jda;

    public MatchHandler(JDA jda) {
        this.jda = jda;
        new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(this::process, 1, 1, TimeUnit.SECONDS);
    }

    public void queue(GuildConfiguration configuration) {
        QSLogger.log(configuration, "New match has been queued.");
        if (!queuedMatches.containsKey(configuration)) {
            queuedMatches.put(configuration, new ArrayList<>());
        }
        queuedMatches.get(configuration).add(System.currentTimeMillis());
    }

    private void process() {
        try {
            Iterator<GuildConfiguration> queueConfigurationIterator = queuedMatches.keySet().iterator();
            while (queueConfigurationIterator.hasNext()) {
                GuildConfiguration configuration = queueConfigurationIterator.next();
                List<Long> queued = queuedMatches.get(configuration);
                Iterator<Long> queueIterator = queued.iterator();
                while (queueIterator.hasNext()) {
                    if (isMatchReady(queueIterator.next(), configuration.getTimeout())) {
                        startMatch(configuration);

                        queueIterator.remove();
                        if (queued.size() <= 1) {
                            queueConfigurationIterator.remove();
                        }
                    }

                }

            }

            Iterator<GuildConfiguration> matchConfigurationIterator = startedMatches.keySet().iterator();
            while (matchConfigurationIterator.hasNext()) {
                GuildConfiguration configuration = matchConfigurationIterator.next();
                List<MatchCollector> started = startedMatches.get(configuration);
                Iterator<MatchCollector> collectorIterator = started.iterator();

                while (collectorIterator.hasNext()) {
                    MatchCollector collector = collectorIterator.next();
                    if (collector.matchStarted()) {
                        jda.removeEventListener(collector);

                        collector.cleanup();
                        collectorIterator.remove();
                        if (started.size() <= 1) {
                            matchConfigurationIterator.remove();
                        }
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private boolean isMatchReady(long added, int timeout) {
        return System.currentTimeMillis() - added >= timeout * 1000;
    }

    private void startMatch(GuildConfiguration configuration) {
        QSLogger.log(configuration, "Starting new match!");
        voiceCountdownHandler.countdown(configuration.getGuild(), configuration.getCountdownChannel());
        if (!startedMatches.containsKey(configuration)) {
            startedMatches.put(configuration, new ArrayList<>());
        }
        MatchCollector collector = new MatchCollector(configuration);
        jda.addEventListener(collector);
        startedMatches.get(configuration).add(collector);
    }

}
