package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.Messages;
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
public class CommandFooter extends Command {

    private final Copycat copycat;

    /**
     * Readies the footer command.
     * The footer command allows the message footer to be
     * changed. This footer is displayed if private message related
     * say command parameters are used.
     */
    public CommandFooter() {
        super("footer", "command.footer.description", Permission.MANAGE_SERVER, "footer [new footer]", false);
        this.copycat = Copycat.getInstance();
    }

    /**
     * Invokes the footer command. It will either display the current
     * footer or set it to the new footer specified.
     * @param event The message event. Contains all required objects.
     * @param args The arguments, including the command itself.
     */
    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        if(args.length > 1) {
            StringBuilder footerBuilder = new StringBuilder();
            for(int i = 1; i < args.length; i++) {
                footerBuilder.append(args[i]).append(" ");
            }
            String footer = footerBuilder.toString().trim();
            copycat.getDataManager().setGuildValue("footer", guild.getId(), footer);
            channel.sendMessage(Messages.get(guild, "command.footer.set")
                    .replace("{footer}", footer)).queue();
        } else {
            String footer = copycat.getLocalGuilds().get(guild.getId()).getFooter();
            footer = footer == null ? Messages.get(guild, "command.footer.null") :
                    Messages.get(guild, "command.footer.footer")
                            .replace("{footer}", footer);
           channel.sendMessage(footer).queue();
        }
    }

}
