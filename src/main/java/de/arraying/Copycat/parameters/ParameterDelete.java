package de.arraying.Copycat.parameters;

import de.arraying.Copycat.data.DataSay;
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
public class ParameterDelete extends Parameter {

    /**
     * The delete parameter (--d) deletes the original
     * command message.
     */
    public ParameterDelete() {
        super("--d");
    }

    /**
     * Invokes the parameter.
     * @param event The chat event.
     * @param input The current input.
     * @return The string after it has been modified.
     */
    @Override
    public String invoke(GuildMessageReceivedEvent event, String input) {
        DataSay.retrieve(event.getMessage().getId()).setDelete(true);
        return input.replace(getTrigger(), "");
    }

}
