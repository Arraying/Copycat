package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.objects.ObjectEmbedBuilder;
import de.arraying.Copycat.objects.ObjectUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.io.FileUtils;

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
public class CommandStats extends Command {

    private Copycat copycat;
    private ObjectEmbedBuilder embedBuilder;

    public CommandStats() {
        super("stats", "Shows my statistics.", Permission.MESSAGE_WRITE, "stats", false);
        getAliases().add("statistics");
        getAliases().add("info");
        copycat = Copycat.getInstance();
        embedBuilder = ObjectUtils.getInstance().getCopycatBuilder();
        embedBuilder.setDescription("Here are the statistics for Copycat.\n"+
                "Information can be found using the help command.");
        embedBuilder.addField("I am meowing alongside...", "An error occurred.", false);
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent e, String[] args) {
        long memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        String memory = FileUtils.byteCountToDisplaySize(memoryUsage);
        embedBuilder.setFieldValue("I am meowing alongside...", "... "+e.getJDA().getGuilds().size()+" guilds.\n"+
                "... "+e.getJDA().getUsers().size()+" users.\n"+
                "... "+(e.getJDA().getVoiceChannels().size()+e.getJDA().getTextChannels().size())+" channels.\n"+
                "... "+memory+" memory used.\n"+
                "... a lot of friends.");
        e.getChannel().sendMessage(embedBuilder.build()).queue();
    }

}
