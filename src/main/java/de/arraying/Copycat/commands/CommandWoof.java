package de.arraying.Copycat.commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * The woof command, AKA ping commands.
 * Checks the latency of the bot, more or less.
 */
public class CommandWoof extends Command {

    public CommandWoof() {
        super("woof", "Checks how fast I can meow back.", Permission.MESSAGE_WRITE, "woof", false);
        getAliases().add("ping");
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent e, String[] args) {
        long before = System.currentTimeMillis();
        e.getChannel().sendMessage("Did I hear a dog?")
                .queue(message -> message.editMessage("Meow ("+(System.currentTimeMillis()-before)+"ms)!").queue());
    }

}
