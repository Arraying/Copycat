package de.arraying.Copycat.listeners;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.commands.Command;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
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
     * @param event The event.
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        User author = event.getAuthor();
        String command = event.getMessage().getRawContent();
        if(!PermissionUtil.checkPermission(channel, guild.getMember(event.getJDA().getSelfUser()), Permission.MESSAGE_WRITE)
                || !PermissionUtil.checkPermission(channel, guild.getMember(event.getJDA().getSelfUser()), Permission.MESSAGE_EMBED_LINKS)
                || author == null
                || channel == null
                || author.getId().equalsIgnoreCase(event.getJDA().getSelfUser().getId())
                || !copycat.getDataManager().checkSync(event)) {
            return;
        }
        command = command.replaceAll(" +", " ");
        String prefix = copycat.getLocalGuilds().get(guild.getId()).getPrefix().toLowerCase();
        if(command.toLowerCase().startsWith(copycat.getDataConfig().getBotPrefix().toLowerCase())) {
            command = command.substring(copycat.getDataConfig().getBotPrefix().length());
        } else if(command.toLowerCase().startsWith(prefix)) {
            command = command.substring(prefix.length());
        } else {
            return;
        }
        String[] parts = command.split(" ");
        String finalCommand = parts[0].toLowerCase();
        Command commandObject = copycat.getCommands().values().stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(finalCommand) || cmd.getAliases().contains(finalCommand))
                .findFirst().orElse(null);
        if(commandObject != null) {
            commandObject.execute(event, parts);
        }
    }

}
