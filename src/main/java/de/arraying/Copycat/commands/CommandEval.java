package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.Messages;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.utils.SimpleLog;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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
public class CommandEval extends Command {

    private final Copycat copycat;
    private final ScriptEngine scriptEngine;

    /**
     * Readies the eval command. using an evaluation
     * engine called Nashorn, similar to JavaScript.
     * The eval command evaluates any given piece of
     * code, hence making it extremely useful for
     * debugging.
     */
    public CommandEval() {
        super("eval", "command.eval.description", Permission.MESSAGE_WRITE, "eval <code>", true);
        this.getAliases().add("evaluate");
        this.getAliases().add("exec");
        this.getAliases().add("execute");
        this.copycat = Copycat.getInstance();
        this.scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            scriptEngine.eval("var imports = new JavaImporter(java.io, java.lang, java.util, Packages.net.dv8tion.jda.core, "
                    + "Packages.net.dv8tion.jda.core.entities, Packages.net.dv8tion.jda.core.managers);");
            scriptEngine.put("copycat", copycat);
        } catch(ScriptException exception) {
            copycat.getLogger().log(SimpleLog.Level.FATAL, "Importing required libraries caused an error: "+exception.getMessage());
        }
    }

    /**
     * Invokes the eval command which will evaluate the code.
     * @param event The message event. Contains all required objects.
     * @param args The arguments, including the command itself.
     */
    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        if(args.length > 1) {
            scriptEngine.put("jda", event.getJDA());
            scriptEngine.put("e", event);
            scriptEngine.put("event", event);
            Object output;
            StringBuilder inputBuilder = new StringBuilder();
            for(int i = 1; i < args.length; i++) {
                inputBuilder.append(args[i]).append(" ");
            }
            String input = inputBuilder.toString().trim();
            try {
                output = scriptEngine.eval("(" +
                            "function() { with (imports) " +
                                "{\n" + input + "\n} " +
                            "}" +
                        ")();");
            } catch(Exception exception) {
                channel.sendMessage(Messages.get(guild, "command.eval.error")+exception.getMessage()).queue();
                return;
            }
            String outputString;
            if(output == null) {
                outputString = Messages.get(guild, "command.eval.output");
            } else {
                outputString = output.toString();
                if(outputString.length() >= 2048) {
                    channel.sendMessage(Messages.get(guild, "command.eval.length")).queue();
                    return;
                }
            }
            channel.sendMessage(outputString).queue();
        } else {
            channel.sendMessage(getSyntaxMessage(guild)).queue();
        }
    }

}
