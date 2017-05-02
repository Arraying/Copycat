package de.arraying.Copycat.commands;

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
public class CommandWoof extends Command {

    /**
     * Readies the woof command.
     * The woof command shows the time in milliseconds
     * that it takes to send a message and update it.
     */
    public CommandWoof() {
        super("woof", "command.woof.description", Permission.MESSAGE_WRITE, "woof", false);
        this.getAliases().add("ping");
    }

    /**
     * Invokes the woof command. It sends the message and updates it.
     * @param event The message event. Contains all required objects.
     * @param args The arguments, including the command itself.
     */
    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        long before = System.currentTimeMillis();
        channel.sendMessage(Messages.get(guild, "command.woof.before"))
                .queue(message -> message.editMessage(Messages.get(guild, "command.woof.after")
                .replace("{ping}", String.valueOf((System.currentTimeMillis()-before)))).queue());
    }

}
