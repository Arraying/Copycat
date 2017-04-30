package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.Messages;
import de.arraying.Copycat.utils.Utils;
import net.dv8tion.jda.core.Permission;
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

    /**
     * Readies the prefix command.
     */
    public CommandPrefix() {
        super("prefix", "command.prefix.description", Permission.MANAGE_SERVER, "prefix [new prefix]", false);
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent e, String[] args) {
        if(args.length == 2) {
            String prefix = Utils.getInstance().stripFormatting(args[1].replace("{space}", " "));
            Copycat.getInstance().getDataManager().setGuildValue("prefix", e.getGuild().getId(), prefix);
            e.getChannel().sendMessage(Messages.get(e.getGuild(), "command.prefix.set").replace("{prefix}", prefix)).queue();
        } else if(args.length == 1) {
            e.getChannel().sendMessage(Messages.get(e.getGuild(), "command.prefix.prefix").replace("{prefix}",
                    Copycat.getInstance().getLocalGuilds().get(e.getGuild().getId()).getPrefix())).queue();
        } else {
            e.getChannel().sendMessage(getSyntaxMessage(e.getGuild())).queue();
        }
    }

}
