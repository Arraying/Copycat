package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.utils.SimpleLog;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Evaluates a piece of code, using the ScriptEngine.
 * The command is author only.
 */
public class CommandEval extends Command {

    private Copycat copycat;
    private ScriptEngine scriptEngine;

    /**
     * Readies the eval engine, using Nashorn.
     */
    public CommandEval() {
        super("eval", "Allows me to execute crazy things.", Permission.MESSAGE_WRITE, "eval <code>", true);
        getAliases().add("evaluate");
        getAliases().add("exec");
        getAliases().add("execute");
        copycat = Copycat.getInstance();
        scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            scriptEngine.eval("var imports = new JavaImporter(java.io, java.lang, java.util, Packages.net.dv8tion.jda.core, "
                    + "Packages.net.dv8tion.jda.core.entities, Packages.net.dv8tion.jda.core.managers);");
            scriptEngine.put("copycat", copycat);
        } catch(ScriptException e) {
            copycat.getLogger().log(SimpleLog.Level.FATAL, "Importing caused an error.");
            e.printStackTrace();
        }
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent e, String[] args) {
        if(args.length > 1) {
            scriptEngine.put("jda", e.getJDA());
            scriptEngine.put("e", e);
            Object output;
            String input = e.getMessage().getRawContent().substring(copycat.getConfig().getBotPrefix().length());
            input = input.substring(input.indexOf(" "));
            try {
                output = scriptEngine.eval("(function() { with (imports) {\n" + input + "\n} })();");
            } catch (ScriptException ex) {
                e.getChannel().sendMessage("An error occurred: "+ex.getMessage()).queue();
                return;
            }
            String outputString;
            if(output == null) {
                outputString = "I have executed that successfully.";
            } else {
                outputString = output.toString();
                if(outputString.length() >= 2048) {
                    e.getChannel().sendMessage("I was going to send you the output along with a selfie,"+
                            " but my cuteness overload isn't supported by Discord.").queue();
                    return;
                }
            }
            e.getChannel().sendMessage(outputString).queue();
        } else {
            e.getChannel().sendMessage(getSyntaxMessage()).queue();
        }
    }

}
