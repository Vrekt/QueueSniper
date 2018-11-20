package me.vrekt.queuesniper.command;

import me.vrekt.queuesniper.command.commands.HelpCommand;
import me.vrekt.queuesniper.command.commands.RequeueCommand;
import me.vrekt.queuesniper.command.commands.SetupCommand;
import me.vrekt.queuesniper.command.commands.StartCommand;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.match.MatchQueueHandler;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandExecutor {

    private final Map<GuildConfiguration, Map<Command, Long>> guildCommandHistory = new HashMap<>();
    private final List<Command> commands = new ArrayList<>();
    private final JDA jda;

    public CommandExecutor(JDA jda) {
        this.jda = jda;
    }

    public void initializeCommands() {
        System.out.println("Registering commands...");

        final MatchQueueHandler queueHandler = new MatchQueueHandler(jda);
        commands.add(new HelpCommand("help", new String[]{}, 500, jda));
        commands.add(new SetupCommand("setup", new String[]{}, 500, jda));
        commands.add(new StartCommand("start", new String[]{}, 60000, queueHandler, jda));
        commands.add(new RequeueCommand("requeue", new String[]{"req", "restart"}, 30000, queueHandler, jda));
    }

    /**
     * Execute a command (if found)
     *
     * @param input         the input from the member
     * @param from          the member
     * @param sentIn        the channel it was sent in
     * @param configuration the configuration of the guild
     */
    public void executeCommand(String input, Member from, TextChannel sentIn, GuildConfiguration configuration) {
        input = input.replace(".", "");
        String[] arguments = input.split(" ");
        String commandName = arguments[0];

        Command execute = find(commandName);
        if (execute != null) {
            if (!guildCommandHistory.containsKey(configuration)) {
                Map<Command, Long> map = new HashMap<>();
                map.put(execute, System.currentTimeMillis());
                guildCommandHistory.put(configuration, map);
            } else {
                long cooldown = execute.getCooldown();
                Map<Command, Long> history = guildCommandHistory.get(configuration);
                if (history.containsKey(execute)) {
                    long lastExecute = history.get(execute);
                    long elapsed = System.currentTimeMillis() - lastExecute;
                    if (elapsed < cooldown) {
                        sentIn.sendMessage("Slow down! You are executing this command too fast.").queue();
                        return;
                    } else {
                        history.remove(execute);
                    }
                } else {
                    history.put(execute, System.currentTimeMillis());
                }
            }
            execute.execute(arguments, from, sentIn, configuration);
        }
    }

    /**
     * Find the command
     *
     * @param commandName the command name
     * @return the command (if found), null otherwise.
     */
    private Command find(String commandName) {
        return commands.stream().filter(command -> command.compare(commandName)).findAny().orElse(null);
    }

}
