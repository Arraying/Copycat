package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.Messages;
import de.arraying.Copycat.utils.UtilsEmbedBuilder;
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
public class CommandHelp extends Command {

    private final Copycat copycat;
    private final Utils utils;

    /**
     * Creates all the Embeds that can be modified later on.
     */
    public CommandHelp() {
        super("help", "command.help.description", Permission.MESSAGE_WRITE, "help [command]", false);
        getAliases().add("commands");
        copycat = Copycat.getInstance();
        utils = Utils.getInstance();
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent e, String[] args) {
        String prefix = copycat.getLocalGuilds().get(e.getGuild().getId()).getPrefix();
        if(args.length == 1) {
            UtilsEmbedBuilder embedBuilder = utils.getCopycatBuilder();
            embedBuilder.setDescription(Messages.get(e.getGuild(), "command.help.h.description"));
            embedBuilder.addField(Messages.get(e.getGuild(), "command.help.h.usage.title"),
                    Messages.get(e.getGuild(), "command.help.h.usage.value").replace("{prefix}", prefix), false);
            StringBuilder commandsBuilder = new StringBuilder();
            copycat.getCommands().values().stream().filter(command -> !command.isAuthorOnly())
                    .forEach(command -> commandsBuilder.append("- ").append(command.getName()).append("\n"));
            embedBuilder.addField(Messages.get(e.getGuild(), "command.help.h.commands.title"), commandsBuilder.toString(), false);
            String author = copycat.getJda().getUserById(copycat.getDataConfig().getBotAuthor()).getName()+"#"+
                    copycat.getJda().getUserById(copycat.getDataConfig().getBotAuthor()).getDiscriminator();
            embedBuilder.addField(Messages.get(e.getGuild(), "command.help.h.information.title"),
                    Messages.get(e.getGuild(), "command.help.h.information.value").replace("{author}", author), false);
            embedBuilder.setFooter(Messages.get(e.getGuild(), "command.help.h.footer"), null);
            e.getChannel().sendMessage(embedBuilder.build()).queue();
        } else if(args.length == 2) {
            if(copycat.getCommands().containsKey(args[1])) {
                Command command = copycat.getCommands().get(args[1]);
                UtilsEmbedBuilder embedBuilder = utils.getCopycatBuilder();
                embedBuilder.setDescription(Messages.get(e.getGuild(), "command.help.i.description"));
                embedBuilder.addField(Messages.get(e.getGuild(), "command.help.i.name.title"), command.getName(), false);
                embedBuilder.addField(Messages.get(e.getGuild(), "command.help.i.cdescription.title"),
                        Messages.get(e.getGuild(), command.getDescription()), false);
                embedBuilder.addField(Messages.get(e.getGuild(), "command.help.i.permission.title"), command.getPermission().toString(), false);
                embedBuilder.addField(Messages.get(e.getGuild(), "command.help.i.syntax.title"), prefix+command.getSyntax(), false);
                e.getChannel().sendMessage(embedBuilder.build()).queue();
            } else {
                e.getChannel().sendMessage(Messages.get(e.getGuild(), "command.help.unknown").replace("{prefix}", prefix));
            }
        } else {
            e.getChannel().sendMessage(getSyntaxMessage(e.getGuild())).queue();
        }
    }

}
