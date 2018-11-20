package me.vrekt.queuesniper.match;

import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.match.collector.MatchIdCollector;
import me.vrekt.queuesniper.match.countdown.QueueCountdown;
import net.dv8tion.jda.core.JDA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MatchQueueHandler {

    private final Map<GuildConfiguration, List<QueuedMatch>> queuedMatches = new HashMap<>();
    private final Map<GuildConfiguration, List<MatchIdCollector>> startedMatches = new HashMap<>();
    private final QueueCountdown queueCountdown = new QueueCountdown();

    private final JDA jda;

    public MatchQueueHandler(JDA jda) {
        this.jda = jda;
        new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(this::enqueue, 1, 1, TimeUnit.SECONDS);
        System.out.println("Queue thread has been started.");
    }

    public void queue(GuildConfiguration configuration, QueuedMatch match) {
        System.out.println("Enqueueing new match from guild: " + configuration.getGuildId());
        if (!queuedMatches.containsKey(configuration)) {
            queuedMatches.put(configuration, new ArrayList<>());
        }
        queuedMatches.get(configuration).add(match);
    }

    private void enqueue() {
        queuedMatches.forEach((configuration, queuedMatches) -> {
            Iterator<QueuedMatch> queue = queuedMatches.iterator();
            while (queue.hasNext()) {
                QueuedMatch match = queue.next();
                if (match.ready()) {
                    startMatch(match.getGuildConfiguration());
                    queue.remove();
                }
            }
        });

        startedMatches.forEach((configuration, matches) -> {
            Iterator<MatchIdCollector> collectors = matches.iterator();
            while (collectors.hasNext()) {
                MatchIdCollector collector = collectors.next();
                if (collector.finished()) {
                    collector.cleanup(jda);
                    collectors.remove();
                }
            }
        });

    }

    private void startMatch(GuildConfiguration configuration) {
        System.out.println("Starting match from guild: " + configuration.getGuildId());
        queueCountdown.countdown(configuration.getGuild(), configuration.getCountdownChannel());
        if (!startedMatches.containsKey(configuration)) {
            startedMatches.put(configuration, new ArrayList<>());
        }
        startedMatches.get(configuration).add(new MatchIdCollector(configuration, jda));
    }

}
