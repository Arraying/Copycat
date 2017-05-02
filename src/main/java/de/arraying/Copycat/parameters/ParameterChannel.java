package de.arraying.Copycat.parameters;

import de.arraying.Copycat.data.DataSay;
import de.arraying.Copycat.data.DataSayValues;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
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
public class ParameterChannel extends Parameter {

    /**
     * The channel parameter (--c) adds a mentioned
     * text channel to the list of channel receivers.
     */
    public ParameterChannel() {
        super("--c ");
    }

    /**
     * Invokes the parameter.
     * @param event The chat event.
     * @param input The current input.
     * @return The string after it has been modified.
     */
    @Override
    public String invoke(GuildMessageReceivedEvent event, String input) {
        Message message = event.getMessage();
        DataSayValues data = DataSay.retrieve(message.getId());
        if(!message.getMentionedChannels().isEmpty()) {
            message.getMentionedChannels().forEach(channel -> {
                if(PermissionUtil.checkPermission(channel, event.getMember(), Permission.MESSAGE_WRITE)
                        && !data.getChannelReceivers().contains(channel.getId())) {
                    data.getChannelReceivers().add(channel.getId());
                }
            });
        }
        input = input.replace(getTrigger(), "");
        for(TextChannel textChannel : message.getMentionedChannels()) {
            input = input.replace("<#"+textChannel.getId()+">", "");
        }
        return input;
    }

}
