package de.arraying.Copycat.listeners;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.commands.Command;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class ListenerChat extends ListenerAdapter {

    private Copycat copycat;

    /**
     * Constructor to get the copycat instance
     * for ease of access.
     */
    public ListenerChat() {
        copycat = Copycat.getInstance();
    }

    /**
     * Checks if the message is a command.
     * If so, it handles the command accordingly.
     * @param e The event.
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        String command = e.getMessage().getRawContent().toLowerCase();
        if(!PermissionUtil.checkPermission(e.getChannel(), e.getGuild().getMember(e.getJDA().getSelfUser()), Permission.MESSAGE_WRITE)
                || !PermissionUtil.checkPermission(e.getChannel(), e.getGuild().getMember(e.getJDA().getSelfUser()), Permission.MESSAGE_EMBED_LINKS)
                || !command.startsWith(copycat.getConfig().getBotPrefix())
                || e.getAuthor() == null
                || e.getChannel() == null) {
            return;
        }
        command = command.substring(copycat.getConfig().getBotPrefix().length());
        command = command.replaceAll(" +", " ");
        String[] parts = command.split(" ");
        Command commandObject = copycat.getCommands().values().stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(parts[0]) || cmd.getAliases().contains(parts[0]))
                .findFirst().orElse(null);
        if(commandObject != null) {
            commandObject.execute(e, parts);
        }
    }

}
