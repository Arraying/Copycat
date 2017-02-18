package de.arraying.Copycat.listeners;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.commands.Command;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.PermissionUtil;

/**
 * Copyright 2017 Arraying
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
                || e.getChannel() == null
                || e.getAuthor().getId().equalsIgnoreCase(e.getJDA().getSelfUser().getId())) {
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
