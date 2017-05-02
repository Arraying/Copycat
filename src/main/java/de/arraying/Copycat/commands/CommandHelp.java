package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.Messages;
import de.arraying.Copycat.utils.UtilsEmbedBuilder;
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
public class CommandHelp extends Command {

    private final Copycat copycat;
    private final Utils utils;

    /**
     * Readies the help command.
     * The help command gives the bot's command list,
     * as well as general information and command details.
     */
    public CommandHelp() {
        super("help", "command.help.description", Permission.MESSAGE_WRITE, "help [command]", false);
        this.getAliases().add("commands");
        this.copycat = Copycat.getInstance();
        this.utils = Utils.getInstance();
    }

    /**
     * Invokes the help command. It will either display the main help
     * embed or display information concerning specific commands.
     * @param event The message event. Contains all required objects.
     * @param args The arguments, including the command itself.
     */
    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        String prefix = copycat.getLocalGuilds().get(guild.getId()).getPrefix();
        if(args.length == 1) {
            UtilsEmbedBuilder embedBuilder = utils.getCopycatBuilder();
            embedBuilder.setDescription(Messages.get(guild, "command.help.h.description"));
            embedBuilder.addField(Messages.get(guild, "command.help.h.usage.title"),
                    Messages.get(guild, "command.help.h.usage.value")
                            .replace("{prefix}", prefix), false);
            StringBuilder commandsBuilder = new StringBuilder();
            copycat.getCommands().values().stream()
                    .filter(command -> !command.isAuthorOnly())
                    .forEach(command -> commandsBuilder.append("- ").append(command.getName()).append("\n"));
            embedBuilder.addField(Messages.get(guild, "command.help.h.commands.title"), commandsBuilder.toString(), false);
            String author = copycat.getJda().getUserById(copycat.getDataConfig().getBotAuthor()).getName()
                    +"#"+
                    copycat.getJda().getUserById(copycat.getDataConfig().getBotAuthor()).getDiscriminator();
            embedBuilder.addField(Messages.get(guild, "command.help.h.information.title"),
                    Messages.get(guild, "command.help.h.information.value")
                            .replace("{author}", author), false);
            embedBuilder.setFooter(Messages.get(guild, "command.help.h.footer"), null);
            channel.sendMessage(embedBuilder.build()).queue();
        } else if(args.length == 2) {
            if(copycat.getCommands().containsKey(args[1])) {
                Command command = copycat.getCommands().get(args[1]);
                UtilsEmbedBuilder embedBuilder = utils.getCopycatBuilder();
                embedBuilder.setDescription(Messages.get(guild, "command.help.i.description"));
                embedBuilder.addField(Messages.get(guild, "command.help.i.name.title"), command.getName(), false);
                embedBuilder.addField(Messages.get(guild, "command.help.i.cdescription.title"),
                        Messages.get(guild, command.getDescription()), false);
                embedBuilder.addField(Messages.get(guild, "command.help.i.permission.title"), command.getPermission().toString(), false);
                embedBuilder.addField(Messages.get(guild, "command.help.i.syntax.title"), prefix+command.getSyntax(), false);
                channel.sendMessage(embedBuilder.build()).queue();
            } else {
                channel.sendMessage(Messages.get(guild, "command.help.unknown")
                        .replace("{prefix}", prefix));
            }
        } else {
            channel.sendMessage(getSyntaxMessage(guild)).queue();
        }
    }

}
