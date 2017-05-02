package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.Messages;
import de.arraying.Copycat.utils.Utils;
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
public class CommandPrefix extends Command {

    private final Copycat copycat;

    /**
     * Readies the prefix command.
     * The prefix command enables the guild to change
     * the bot's prefix to whatever they desire.
     */
    public CommandPrefix() {
        super("prefix", "command.prefix.description", Permission.MANAGE_SERVER, "prefix [new prefix]", false);
        this.copycat = Copycat.getInstance();
    }

    /**
     * Invokes the prefix command. Either displays the prefix or
     * updates the prefix to the given value.
     * @param event The message event. Contains all required objects.
     * @param args The arguments, including the command itself.
     */
    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        if(args.length == 2) {
            String prefix = Utils.getInstance().stripFormatting(args[1].replace("{space}", " "));
            copycat.getDataManager().setGuildValue("prefix", guild.getId(), prefix);
            channel.sendMessage(Messages.get(guild, "command.prefix.set").replace("{prefix}", prefix)).queue();
        } else if(args.length == 1) {
            channel.sendMessage(Messages.get(guild, "command.prefix.prefix").replace("{prefix}",
                    copycat.getLocalGuilds().get(guild.getId()).getPrefix())).queue();
        } else {
            channel.sendMessage(getSyntaxMessage(guild)).queue();
        }
    }

}
