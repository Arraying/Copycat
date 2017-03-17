package de.arraying.Copycat.parameters;

import de.arraying.Copycat.data.DataSay;
import de.arraying.Copycat.data.DataSayValues;
import de.arraying.Copycat.utils.Utils;
import net.dv8tion.jda.core.Permission;
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
public class ParameterChannelId extends Parameter {

    /**
     * The channel ID parameter (-cid) adds the specified
     * channel ID to the list of receivers.
     */
    public ParameterChannelId() {
        super("-cid");
    }

    @Override
    public String invoke(GuildMessageReceivedEvent e, String input) {
        String value = Utils.getInstance().getParameterValue(input, getTrigger());
        DataSayValues data = DataSay.retrieve(e.getMessage().getId());
        if(!value.equalsIgnoreCase("")) {
            TextChannel textChannel = e.getGuild().getTextChannelById(value);
            if(textChannel != null
                    && PermissionUtil.checkPermission(textChannel, e.getMember(), Permission.MESSAGE_WRITE)
                    && !data.getChannelReceivers().contains(textChannel.getId())) {
                data.getChannelReceivers().add(textChannel.getId());
            }
        }
        return input.replace(getTrigger()+" "+value, "");
    }

}
