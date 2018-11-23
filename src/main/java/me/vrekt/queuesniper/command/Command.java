package me.vrekt.queuesniper.command;

import me.vrekt.queuesniper.guild.GuildConfiguration;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Arrays;
import java.util.List;

public abstract class Command {

    protected final JDA jda;

    private final String name;
    private final List<String> aliases;
    private final long cooldown;

    protected Command(String name, String[] aliases, long cooldown, JDA jda) {
        this.name = name;
        this.aliases = Arrays.asList(aliases);

        this.jda = jda;
        this.cooldown = cooldown;
    }

    /**
     * Compare the name given to this command.
     *
     * @param name the name
     * @return true, if the name matches with this command.
     */
    boolean compare(String name) {
        return this.name.equalsIgnoreCase(name) || aliases.contains(name);
    }

    long getCooldown() {
        return cooldown;
    }

    public abstract boolean execute(String[] args, final Member from, final TextChannel sentIn, final GuildConfiguration configuration);

}
