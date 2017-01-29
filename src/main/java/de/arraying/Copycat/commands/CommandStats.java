package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.objects.ObjectEmbedBuilder;
import de.arraying.Copycat.objects.ObjectUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.io.FileUtils;

/**
 * Shows bot statistics.
 */
public class CommandStats extends Command {

    private Copycat copycat;
    private ObjectEmbedBuilder embedBuilder;

    public CommandStats() {
        super("stats", "Shows my statistics.", Permission.MESSAGE_WRITE, "stats", false);
        copycat = Copycat.getInstance();
        embedBuilder = ObjectUtils.getInstance().getCopycatBuilder();
        embedBuilder.setDescription("Here are the statistics for Copycat.\n"+
                "Information can be found using the help command.");
        embedBuilder.addField("I am meowing alongside...", "An error occurred.", false);
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent e, String[] args) {
        long memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        String memory = FileUtils.byteCountToDisplaySize(memoryUsage);
        embedBuilder.setFieldValue("I am meowing alongside...", "..."+e.getJDA().getGuilds().size()+" guilds.\n"+
                "... "+e.getJDA().getUsers().size()+" users.\n"+
                "... "+(e.getJDA().getVoiceChannels().size()+e.getJDA().getTextChannels().size())+" channels.\n"+
                "... "+memory+" memory used."+
                "... a lot of friends.");
        e.getChannel().sendMessage(embedBuilder.build()).queue();
    }

}
