package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import net.dv8tion.jda.core.Permission;
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
     */
    public CommandQueue() {
        super("queue", "Shows the current queue status.", Permission.MESSAGE_WRITE, "queue", false);
        copycat = Copycat.getInstance();
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent e, String[] args) {
        if(copycat.getQueue().containsKey(e.getGuild().getId())) {
            int leftQueue = copycat.getQueue().get(e.getGuild().getId());
            e.getChannel().sendMessage("You currently have "+leftQueue+" messages that are still in the queue.").queue();
        } else {
            e.getChannel().sendMessage("You currently do not have any messages queued.").queue();
        }
    }

}
