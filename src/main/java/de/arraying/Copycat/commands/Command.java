package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.Messages;
import lombok.Data;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.util.ArrayList;

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
public @Data abstract class Command {

    private final String name;
    private final String description;
    private final Permission permission;
    private final String syntax;
    private final boolean authorOnly, enabled;
    private final ArrayList<String> aliases;

    /**
     * Creates a new command object.
     * @param name The name (trigger) of the command.
     * @param description The description of the command.
     * @param permission The permission required for the command.
     * @param syntax The syntax of the command.
     * @param authorOnly Whether or not the command is for the developer only.
     */
    public Command(String name, String description, Permission permission, String syntax, boolean authorOnly) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.syntax = syntax;
        this.authorOnly = authorOnly;
        this.enabled = true;
        this.aliases = new ArrayList<>();
    }

    /**
     * The method that the command objects override. It contains
     * the actual command of the code, and it is invoked when the
     * command pre-processing is finished.
     * @param event The message event. Contains all required objects.
     * @param args The arguments, including the command itself.
     *             It should always be the length of at least 1.
     */
    public abstract void onCommand(GuildMessageReceivedEvent event, String[] args);

    /**
     * This method pre-processes the command and does some routine
     * checks so that they do not need to me repeated multiple times.
     * @param event The message event.
     * @param args The arguments, including the command itself.
     */
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        if(description == null
                || permission == null
                || syntax == null) {
            return;
        }
        Member member = event.getMember();
        Guild guild = event.getGuild();
        TextChannel channel = event.getChannel();
        if(enabled) {
            if(authorOnly && !member.getUser().getId().equalsIgnoreCase(Copycat.getInstance().getDataConfig().getBotAuthor())) {
                return;
            }
            if(PermissionUtil.checkPermission(channel, member, permission)) {
                onCommand(event, args);
                Copycat.getInstance().getLogger()
                        .log(SimpleLog.Level.INFO,member.getUser().getName()+" executed "+name+" in "+guild.getName()+".");
            } else {
                channel.sendMessage(Messages.get(guild, "command.noperm").replace("{permission}", permission.toString())).queue();
            }
        } else {
            channel.sendMessage(Messages.get(guild, "command.disabled")).queue();
        }
    }

    /**
     * Get the no syntax message.
     * @param guild The guild.
     * @return The message.
     */
    public String getSyntaxMessage(Guild guild) {
        return Messages.get(guild, "command.syntax").replace("{syntax}", getSyntax());
    }

}
