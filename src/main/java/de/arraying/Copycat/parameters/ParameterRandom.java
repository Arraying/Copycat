package de.arraying.Copycat.parameters;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.Random;

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
public class ParameterRandom extends Parameter {

    /**
     * The random parameter (-r) takes the input
     * and splits it into an array using the
     * character ";". A random string from that
     * array is then returned.
     */
    public ParameterRandom() {
        super("-r");
    }

    @Override
    public String invoke(GuildMessageReceivedEvent e, String input) {
        Random random = new Random();
        String[] parts = input.split(";");
        return parts[random.nextInt(parts.length)];
    }

}
