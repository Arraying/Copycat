package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.Messages;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

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
public class CommandQueue extends Command {

    private final Copycat copycat;

    /**
     * Readies the queue command.
     * The queue command shows how many messages still
     * need to be sent after executing the say command.
     * The reason for this is Discord's ratelimit.
     */
    public CommandQueue() {
        super("queue", "command.queue.description", Permission.MESSAGE_WRITE, "queue", false);
        this.copycat = Copycat.getInstance();
    }

    /**
     * Invokes the queue command. It displays the amount of messages
     * that are still in the queue.
     * @param event The message event. Contains all required objects.
     * @param args The arguments, including the command itself.
     */
    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        if(copycat.getQueue().containsKey(guild.getId())) {
            int leftQueue = copycat.getQueue().get(guild.getId());
            channel.sendMessage(Messages.get(guild, "command.queue.queue").replace("{messages}", String.valueOf(leftQueue))).queue();
        } else {
            channel.sendMessage(Messages.get(guild, "command.queue.empty")).queue();
        }
    }

}
