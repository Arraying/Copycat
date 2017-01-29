package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.objects.ObjectEmbedBuilder;
import de.arraying.Copycat.objects.ObjectUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * The help command.
 * Shows general help menu or command information.
 */
public class CommandHelp extends Command {

    private Copycat copycat;
    private ObjectUtils utils;
    private ObjectEmbedBuilder embedBuilder, commandBuilder;
    private boolean goneOver;

    /**
     * Creates all the Embeds that can be modified later on.
     */
    public CommandHelp() {
        super("help", "Shows how you can play with me.", Permission.MESSAGE_WRITE, "help [command]", false);
        getAliases().add("commands");
        copycat = Copycat.getInstance();
        utils = ObjectUtils.getInstance();
        embedBuilder = utils.getCopycatBuilder();
        embedBuilder.setDescription("Hey! I'm Copycat. As you can tell, I copy people. I was made to send messages - in an advanced way. "+
                "I don't ignore bots, they have feelings too, so you can hook me to them and make me do what they cannot do. "+
                "However, my main role is to be an entertaining and loyal companion to all guilds.");
        embedBuilder.addField("Usage", "You can do '{prefix}help <command>' for information. \n"+
                "A list of commands is below.", false);
        embedBuilder.addField("Commands", "{commands}", false);
        embedBuilder.addField("Information", "- Developed by [{author}](http://arraying.de)\n"+
                "- My code can be found [here](https://github.com/Wiildanimal/Copycat)\n"+
                "- You can invite me [here](https://discordapp.com/oauth2/authorize?client_id=273121537132068884&scope=bot&permissions=27648)\n"+
                "- Help can be found [here](http://guild.arraybot.xyz).", false);
        commandBuilder = utils.getCopycatBuilder();
        commandBuilder.setDescription("Here is information for the command.");
        commandBuilder.addField("Name", "An error occurred.", false);
        commandBuilder.addField("Description", "An error occurred.", false);
        commandBuilder.addField("Permission", "An error occurred.", false);
        commandBuilder.addField("Syntax", "An error occurred.", false);
        goneOver = false;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent e, String[] args) {
        if(!goneOver) {
            String author = copycat.getJda().getUserById(copycat.getConfig().getBotAuthor()).getName()+"#"+
                    copycat.getJda().getUserById(copycat.getConfig().getBotAuthor()).getDiscriminator();
            embedBuilder.replaceFieldValue("Information", "{author}", author);
            embedBuilder.replaceFieldValue("Usage", "{prefix}", copycat.getConfig().getBotPrefix());
            StringBuilder stringBuilder = new StringBuilder();
            copycat.getCommands().keySet().forEach(command -> stringBuilder.append("- "+command+"\n"));
            embedBuilder.replaceFieldValue("Commands", "{commands}", stringBuilder.toString());
            goneOver = true;
        }
        if(args.length == 1) {
            e.getChannel().sendMessage(embedBuilder.build()).queue();
        } else if(args.length == 2) {
            if(copycat.getCommands().containsKey(args[1])) {
                Command command = copycat.getCommands().get(args[1]);
                commandBuilder.setFieldValue("Name", command.getName());
                commandBuilder.setFieldValue("Description", command.getDescription());
                commandBuilder.setFieldValue("Permission", command.getPermission().toString());
                commandBuilder.setFieldValue("Syntax", copycat.getConfig().getBotPrefix()+command.getSyntax());
                e.getChannel().sendMessage(commandBuilder.build()).queue();
            } else {
                e.getChannel().sendMessage("I don't recognise that command. Do '"+copycat.getConfig().getBotPrefix()+"help' for a list of commands.");
            }
        } else {
            e.getChannel().sendMessage(getSyntaxMessage()).queue();
        }
    }

}
