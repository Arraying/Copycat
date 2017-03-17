package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import lombok.Data;
import net.dv8tion.jda.core.Permission;
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
     * The command object constructor.
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
     * The method that the command objects
     * override which contains the command
     * actions.
     */
    public abstract void onCommand(GuildMessageReceivedEvent e, String[] args);

    /**
     * The method that takes care of command checks.
     * @param e The event.
     * @param args The arguments.
     */
    public void execute(GuildMessageReceivedEvent e, String[] args) {
        if(description == null
                || permission == null
                || syntax == null) {
            return;
        }
        if(enabled) {
            if(authorOnly && !e.getMember().getUser().getId().equalsIgnoreCase(Copycat.getInstance().getDataConfig().getBotAuthor())) {
                return;
            }
            if(PermissionUtil.checkPermission(e.getChannel(), e.getMember(), permission)) {
                onCommand(e, args);
                Copycat.getInstance().getLogger()
                        .log(SimpleLog.Level.INFO,e.getMember().getUser().getName()+" executed "+name+" in "+e.getGuild().getName()+".");
            } else {
                e.getChannel().sendMessage("You do not have permission to execute this command, you require "+permission.toString()+".");
            }
        } else {
            e.getChannel().sendMessage("That command is currently disabled, sorry for the inconvenience.").queue();
        }
    }

    /**
     * Get the no syntax message.
     * @return The message.
     */
    public String getSyntaxMessage() {
        return "Incorrect syntax. Do '"+Copycat.getInstance().getDataConfig().getBotPrefix()+syntax+"'.";
    }

}
