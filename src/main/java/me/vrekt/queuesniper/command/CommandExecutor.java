package me.vrekt.queuesniper.command;

import me.vrekt.queuesniper.QSLogger;
import me.vrekt.queuesniper.command.commands.HelpCommand;
import me.vrekt.queuesniper.command.commands.RequeueCommand;
import me.vrekt.queuesniper.command.commands.SetupCommand;
import me.vrekt.queuesniper.command.commands.StartCommand;
import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.match.GuildMatchHandler;
import me.vrekt.queuesniper.result.MessageActionHandler;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandExecutor {

    private final Map<String, Map<String, Long>> guildCommandHistory = new HashMap<>();
    private final List<Command> commands = new ArrayList<>();
    private final JDA jda;

    public CommandExecutor(JDA jda) {
        this.jda = jda;
    }

    public void initializeCommands(GuildMatchHandler matchHandler) {
        QSLogger.log("Registering commands {Help, Setup, Start, Requeue}");

        commands.add(new SetupCommand("setup", new String[]{"register"}, 1000, jda));
        commands.add(new HelpCommand("help", new String[]{"halp", "permissions"}, 1000, jda));

        commands.add(new StartCommand("start", new String[]{}, 30000, matchHandler));
        commands.add(new RequeueCommand("requeue", new String[]{"req", "restart"}, 30000, matchHandler));
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
            String guildId = configuration.getGuildId();

            // command never executed in the guild
            if (!guildCommandHistory.containsKey(guildId)) {
                guildCommandHistory.put(guildId, new HashMap<>());
            }

            Map<String, Long> history = guildCommandHistory.get(guildId);
            long lastExecuteTime = history.getOrDefault(execute.name, -1L);
            if (lastExecuteTime != -1) {
                long time = System.currentTimeMillis() - lastExecuteTime;
                if (time < execute.cooldown) {
                    MessageActionHandler.sendMessageToChannel(sentIn, "This command has a " + execute.cooldown / 1000 + " second " +
                            "cool-down.");
                    return;
                } else {
                    history.remove(execute.name);
                }
            }

            execute.execute(arguments, from, sentIn, configuration);
            if (!execute.failed) {
                // if the command didn't fail to execute add it to the history map
                history.put(execute.name, System.currentTimeMillis());
            }

            guildCommandHistory.put(guildId, history);
        } else {
            MessageActionHandler.sendMessageToChannel(sentIn, "The command " + input + " does not exist! Refer to .help if you need help " +
                    "using QueueSniper.");
        }
    }

    /**
     * Find the command
     *
     * @param commandName the command name
     * @return the command (if found), null otherwise.
     */
    private Command find(String commandName) {
        return commands.stream().filter(command -> command.thisCommand(commandName)).findAny().orElse(null);
    }

}
