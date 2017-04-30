package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.Messages;
import de.arraying.Copycat.data.DataGuild;
import de.arraying.Copycat.utils.Utils;
import de.arraying.Copycat.utils.UtilsEmbedBuilder;
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
public class CommandLanguage extends Command {

    public CommandLanguage() {
        super("language", "command.language.description", Permission.MANAGE_SERVER, "language [new language]", false);
        getAliases().add("lang");
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent e, String[] args) {
        if(args.length == 2) {
            if(Messages.getLanguages().contains(args[1].toLowerCase())) {
                Copycat.getInstance().getDataManager().setGuildValue("language", e.getGuild().getId(), args[1].toLowerCase());
                e.getChannel().sendMessage(Messages.get(e.getGuild(), "command.language.set")
                        .replace("{language}", args[1].toLowerCase())).queue();
            } else {
                e.getChannel().sendMessage(Messages.get(e.getGuild(), "command.language.exists")).queue();
            }
        } else {
            DataGuild localGuild = Copycat.getInstance().getLocalGuilds().get(e.getGuild().getId());
            UtilsEmbedBuilder embedBuilder = Utils.getInstance().getCopycatBuilder();
            embedBuilder.setDescription(Messages.get(e.getGuild(), "command.language.l.description")
                    .replace("{prefix}", localGuild.getPrefix())
                    .replace("{language}", localGuild.getLanguage()));
            StringBuilder languageBuilder = new StringBuilder();
            Messages.getLanguages().forEach(language -> languageBuilder.append("- ").append(language).append("\n"));
            embedBuilder.addField(Messages.get(e.getGuild(), "command.language.l.list.title"),
                    languageBuilder.toString(), false);
            embedBuilder.setFooter(Messages.get(e.getGuild(), "command.language.l.footer"), null);
            e.getChannel().sendMessage(embedBuilder.build()).queue();
        }
    }

}
