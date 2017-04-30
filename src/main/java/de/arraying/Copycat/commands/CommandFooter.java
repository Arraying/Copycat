package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.Messages;
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
public class CommandFooter extends Command {

    /**
     * Readies the footer command.
     */
    public CommandFooter() {
        super("footer", "command.footer.description", Permission.MANAGE_SERVER, "footer [new footer]", false);
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent e, String[] args) {
        if(args.length > 1) {
            StringBuilder footerBuilder = new StringBuilder();
            for(int i = 1; i < args.length; i++) {
                footerBuilder.append(args[i]).append(" ");
            }
            String footer = footerBuilder.toString().trim();
            Copycat.getInstance().getDataManager().setGuildValue("footer", e.getGuild().getId(), footer);
            e.getChannel().sendMessage(Messages.get(e.getGuild(), "command.footer.set").replace("{footer}", footer)).queue();
        } else {
            String footer = Copycat.getInstance().getLocalGuilds().get(e.getGuild().getId()).getFooter();
            footer = footer == null ? Messages.get(e.getGuild(), "command.footer.null") :
                    Messages.get(e.getGuild(), "command.footer.footer").replace("{footer}", footer);
            e.getChannel().sendMessage(footer).queue();
        }
    }

}
