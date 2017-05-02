package de.arraying.Copycat.commands;

import de.arraying.Copycat.Messages;
import de.arraying.Copycat.utils.UtilsEmbedBuilder;
import de.arraying.Copycat.utils.Utils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
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

    /**
     * Readies the stats command.
     * The statistics command shows general bot
     * statistics, however no information.
     */
    public CommandStats() {
        super("stats", "command.stats.description", Permission.MESSAGE_WRITE, "stats", false);
        this.getAliases().add("statistics");
        this.getAliases().add("info");
    }

    /**
     * Invokes the stats command. It sends a statistics embed
     * to the current text channel.
     * @param event The message event. Contains all required objects.
     * @param args The arguments, including the command itself.
     */
    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        JDA jda = event.getJDA();
        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        UtilsEmbedBuilder embedBuilder = Utils.getInstance().getCopycatBuilder();
        embedBuilder.setDescription(Messages.get(guild, "command.stats.s.description"));
        long memoryUsage = Runtime.getRuntime().totalMemory() -
                Runtime.getRuntime().freeMemory();
        String memory = FileUtils.byteCountToDisplaySize(memoryUsage);
        int channels = jda.getTextChannels().size() +
               jda.getVoiceChannels().size();
        embedBuilder.addField(Messages.get(guild, "command.stats.s.main.title"),
                Messages.get(guild, "command.stats.s.main.value")
                    .replace("{guilds}", String.valueOf(jda.getGuilds().size()))
                    .replace("{users}", String.valueOf(jda.getUsers().size()))
                    .replace("{channels}", String.valueOf(channels))
                    .replace("{memory}", memory)
                , false);
        channel.sendMessage(embedBuilder.build()).queue();
    }

}
