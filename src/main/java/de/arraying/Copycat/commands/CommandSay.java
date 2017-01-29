package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.objects.ObjectUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.List;

public class CommandSay extends Command {

    private Copycat copycat;
    private ObjectUtils utils;

    public CommandSay() {
        super("say", "My main feature. For more information, check my docs.", Permission.MESSAGE_WRITE, "say <input>", false);
        copycat = Copycat.getInstance();
        utils = ObjectUtils.getInstance();
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent e, String[] args) {
        if(args.length > 1) {
            String input = e.getMessage().getRawContent().substring(copycat.getConfig().getBotPrefix().length());
            input = input.substring(input.indexOf(" "));
            String receiver = "nutin";
            boolean silent = false;
            if(input.contains("-d")) {
                input = input.replace("-d", "");
                if(PermissionUtil.checkPermission(e.getChannel(), e.getGuild().getMember(e.getJDA().getSelfUser()), Permission.MESSAGE_MANAGE)) {
                    e.getMessage().deleteMessage().queue();
                }
            }
            if(input.contains("-s")) {
                input = input.replace("-s", "");
                silent = true;
            }
            if(input.contains("-pmn")) {
                String value = utils.getParameterValue(input, "-pmn");
                if(!value.equalsIgnoreCase("")) {
                    receiver = value;
                }
                input = input.replace("-pmn "+value, "");
            }
            if(input.contains("-pmid")) {
                String value = utils.getParameterValue(input, "-pmid");
                if(!value.equalsIgnoreCase("")) {
                    receiver = value;
                }
                input = input.replace("-pmid "+value, "");
            }
            if(input.contains("-pm")) {
                String value = utils.getParameterValue(input, "-pm");
                if(!value.equalsIgnoreCase("")
                        && value.startsWith("<")
                        && value.endsWith(">")
                        && value.indexOf("@") == 1) {
                    String newValue = value.substring(2);
                    receiver = newValue.substring(0, newValue.length()-1);
                }
                input = input.replace("-pm "+value, "");
            }
            if(input.isEmpty()
                    || input.equalsIgnoreCase(" ")) {
                e.getChannel().sendMessage("The message is empty, add some kittens to it, please.").queue();
                return;
            }
            input = utils.stripFormatting(input);
            if(e.getGuild().getTextChannelById(receiver) != null) {
                sendMessage(e.getGuild().getTextChannelById(receiver), input);
            } else if(e.getJDA().getUserById(receiver) != null) {
                sendMessage(e.getJDA().getUserById(receiver), input);
            } else if(e.getJDA().getUsersByName(receiver, true).size() > 0) {
                sendMessage(e.getJDA().getUsersByName(receiver, true), input);
            }
            else {
                e.getChannel().sendMessage(input).queue();
            }
            if(!silent) {
                e.getChannel().sendMessage("I have meowed that message.").queue();
            }
        } else {
            e.getChannel().sendMessage(getSyntaxMessage()).queue();
        }
    }

    private void sendMessage(User user, final String input) {
        user.openPrivateChannel().queue(pm -> pm.sendMessage(input).queue());
    }

    private void sendMessage(TextChannel textChannel, final String input) {
        textChannel.sendMessage(input).queue();
    }

    private void sendMessage(List<User> users, final String input) {
        users.forEach(user -> sendMessage(user, input));
    }

}
