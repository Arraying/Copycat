package de.arraying.Copycat.parameters;

import de.arraying.Copycat.data.DataSay;
import de.arraying.Copycat.data.DataSayValues;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
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
public class ParameterPrivateMessage extends Parameter {

    /**
     * The private message parameter (-pm) adds
     * a mentioned user to the user receivers.
     */
    public ParameterPrivateMessage() {
        super("-pm ");
    }

    @Override
    public String invoke(GuildMessageReceivedEvent e, String input) {
        DataSayValues data = DataSay.retrieve(e.getMessage().getId());
        if(!e.getMessage().getMentionedUsers().isEmpty()
                && PermissionUtil.checkPermission(e.getChannel(), e.getMember(), Permission.MESSAGE_MENTION_EVERYONE)) {
            e.getMessage().getMentionedUsers().forEach(user -> {
                if(!data.getUserReceivers().contains(user.getId())
                        && !e.getJDA().getSelfUser().getId().equalsIgnoreCase(user.getId())) {
                    data.getUserReceivers().add(user.getId());
                }
            });
        }
        input = input.replace(getTrigger(), "");
        for(User user : e.getMessage().getMentionedUsers()) {
            Member member = e.getGuild().getMember(user);
            if(member.getNickname() != null) {
                input = input.replace("<@!"+user.getId()+">", "");
            } else {
                input = input.replace("<@"+user.getId()+">", "");
            }
        }
        return input;
    }

}
