package de.arraying.Copycat.listeners;

import com.mashape.unirest.http.exceptions.UnirestException;
import de.arraying.Copycat.Copycat;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

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
public class ListenerChange extends ListenerAdapter {

    private final Copycat copycat;

    /**
     * Constructor to get the copycat instance
     * for ease of access.
     */
    public ListenerChange() {
        this.copycat = Copycat.getInstance();
    }

    /**
     * When the bot joins a guild.
     * @param e The event instance.
     */
    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        try {
            copycat.getRequester().sendPostRequests(e.getJDA().getSelfUser().getId(), e.getJDA().getGuilds().size());
        } catch(UnirestException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * When the bot leaves a guild.
     * @param e The event instance.
     */
    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        try {
            copycat.getRequester().sendPostRequests(e.getJDA().getSelfUser().getId(), e.getJDA().getGuilds().size());
        } catch(UnirestException ex) {
            ex.printStackTrace();
        }
    }

}
