package me.vrekt.queuesniper.command;

import me.vrekt.queuesniper.guild.GuildConfiguration;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Arrays;
import java.util.List;

public abstract class Command {

    protected final String name;
    protected final List<String> aliases;
    protected final long cooldown;
    protected JDA jda;

    protected boolean failed;

    protected Command(String name, String[] aliases, long cooldown, JDA jda) {
        this.name = name;
        this.aliases = Arrays.asList(aliases);

        this.jda = jda;
        this.cooldown = cooldown;
    }

    protected Command(String name, String[] aliases, long cooldown) {
        this.name = name;
        this.aliases = Arrays.asList(aliases);

        this.cooldown = cooldown;
    }

    /**
     * Check if a command name matches this one or one of the aliases.
     *
     * @param name the name
     * @return false if the name does not match this one and it is not in the alias list.
     */
    boolean thisCommand(String name) {
        return this.name.equalsIgnoreCase(name) || aliases.contains(name);
    }

    /**
     * Execute the command.
     *
     * @param args          the arguments
     * @param from          the member that sent the command
     * @param sentIn        the text channel that the command was sent in
     * @param configuration the configuration of the guild it was sent in
     */
    public abstract void execute(String[] args, final Member from, final TextChannel sentIn, final GuildConfiguration configuration);

}
