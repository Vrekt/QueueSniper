package me.vrekt.queuesniper.command;

import me.vrekt.queuesniper.QSLogger;
import me.vrekt.queuesniper.command.commands.HelpCommand;
import me.vrekt.queuesniper.command.commands.RequeueCommand;
import me.vrekt.queuesniper.command.commands.SetupCommand;
import me.vrekt.queuesniper.command.commands.StartCommand;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.match.MatchHandler;
import me.vrekt.queuesniper.permission.PermissionChecker;
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
        QSLogger.log("Registering commands {Help, Setup, Start, Requeue}");

        commands.add(new SetupCommand("setup", new String[]{"register"}, 1000, jda));
        commands.add(new HelpCommand("help", new String[]{"halp", "permissions"}, 1000, jda));

        MatchHandler handler = new MatchHandler(jda);
        commands.add(new StartCommand("start", new String[]{"queue", "run"}, 30000, handler, jda));
        commands.add(new RequeueCommand("requeue", new String[]{"requeue", "req", "restart"}, 30000, handler, jda));
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

        if (!PermissionChecker.hasTextPermissions(sentIn, configuration.getSelf())) {
            return;
        }

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
                        sentIn.sendMessage("This command has a " + cooldown / 1000 + " second cooldown.").queue();
                        return;
                    } else {
                        history.remove(execute);
                    }
                } else {
                    history.put(execute, System.currentTimeMillis());
                }
            }
            boolean result = execute.execute(arguments, from, sentIn, configuration);
            if (!result) {
                // remove from cooldown
                if (guildCommandHistory.containsKey(configuration)) {
                    guildCommandHistory.get(configuration).remove(execute);
                }
            }
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
