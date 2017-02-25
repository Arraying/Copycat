package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.objects.ObjectEmbedBuilder;
import de.arraying.Copycat.objects.ObjectUtils;
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
        embedBuilder.addField("Information", "- Developed by [{author}](http://arraying.de) in Java using [JDA](https://github.com/DV8FromTheWorld/JDA).\n"+
                "- My code can be found [here](https://github.com/Wiildanimal/Copycat)\n"+
                "- You can invite me [here](https://discordapp.com/oauth2/authorize?client_id=273121537132068884&scope=bot&permissions=27648)\n"+
                "- Help can be found [here](http://guild.arraybot.xyz).", false);
        embedBuilder.setFooter("Thank you to DinosParkour and Carbonitex.", null);
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
            copycat.getCommands().values().stream().filter(command -> !command.isAuthorOnly())
                    .forEach(command -> stringBuilder.append("- ").append(command.getName()).append("\n"));
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
