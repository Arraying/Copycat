package de.arraying.Copycat.parameters;

import de.arraying.Copycat.data.DataSay;
import net.dv8tion.jda.core.entities.User;
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
public class ParameterPrivateMessageEveryone extends Parameter {

    /**
     * The private message everyone parameter (-pme) sends
     * the message as a private message to everyone in the
     * guild.
     */
    public ParameterPrivateMessageEveryone() {
        super("-pme");
    }

    @Override
    public String invoke(GuildMessageReceivedEvent e, String input) {
        e.getGuild().getMembers().forEach(member -> {
            User user = member.getUser();
            if(!DataSay.retrieve(e.getMessage().getId()).getUserReceivers().contains(user.getId())
                    && !e.getJDA().getSelfUser().getId().equalsIgnoreCase(user.getId())) {
                DataSay.retrieve(e.getMessage().getId()).getUserReceivers().add(user.getId());
            }
        });
        return input.replace(getTrigger(), "");
    }

}
