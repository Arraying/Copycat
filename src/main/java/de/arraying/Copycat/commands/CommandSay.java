package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.objects.ObjectUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
            List<Member> receivers = new ArrayList<>();
            Random randomObject = new Random();
            boolean silent = false, multiple = false, random = false;
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
            if(input.contains("-r")) {
                input = input.replace("-r", "");
                random = true;
            }
            if(input.contains("-cid")) {
                String value = utils.getParameterValue(input, "-cid");
                if(!value.equalsIgnoreCase("")) {
                    TextChannel textChannel = e.getGuild().getTextChannelById(value);
                    if(textChannel != null
                            && PermissionUtil.checkPermission(textChannel, e.getMember(), Permission.MESSAGE_WRITE)) {
                        receiver = value;
                    }
                }
                input = input.replace("-cid "+value, "");
            } else if(input.contains("-c")) {
                if(!e.getMessage().getMentionedChannels().isEmpty()
                        && PermissionUtil.checkPermission(e.getMessage().getMentionedChannels().get(0), e.getMember(), Permission.MESSAGE_WRITE)) {
                    receiver = e.getMessage().getMentionedChannels().get(0).getId();
                }
                input = input.replace("-c", "");
                input = input.replace("<#"+receiver+">", "");
            }
            if(input.contains("-pmn")) {
                String value = utils.getParameterValue(input, "-pmn");
                if(!value.equalsIgnoreCase("")
                        && PermissionUtil.checkPermission(e.getChannel(), e.getMember(), Permission.MESSAGE_MENTION_EVERYONE)) {
                    receiver = value;
                }
                input = input.replace("-pmn "+value, "");
            } else if(input.contains("-pmid")) {
                String value = utils.getParameterValue(input, "-pmid");
                if(!value.equalsIgnoreCase("")
                        && PermissionUtil.checkPermission(e.getChannel(), e.getMember(), Permission.MESSAGE_MENTION_EVERYONE)) {
                    receiver = value;
                }
                input = input.replace("-pmid "+value, "");
            }
            else if(input.contains("-pm")) {
                if(!e.getMessage().getMentionedUsers().isEmpty()
                        && PermissionUtil.checkPermission(e.getChannel(), e.getMember(), Permission.MESSAGE_MENTION_EVERYONE)) {
                    multiple = true;
                    e.getMessage().getMentionedUsers().forEach(user -> receivers.add(e.getGuild().getMember(user)));
                }
                input = input.replace("-pm", "");
                for(User user : e.getMessage().getMentionedUsers()) {
                    Member member = e.getGuild().getMember(user);
                    if(member.getNickname() != null) {
                        input = input.replace("<@!"+user.getId()+">", "");
                    } else {
                        input = input.replace("<@"+user.getId()+">", "");
                    }
                }
            }
            input = input.trim();
            if(input.isEmpty()
                    || input.equalsIgnoreCase(" ")) {
                e.getChannel().sendMessage("The message is empty, add some kittens to it, please.").queue();
                return;
            }
            if(random) {
                String[] randomParts = input.split(";");
                input = randomParts[randomObject.nextInt(randomParts.length)];
            }
            input = utils.stripFormatting(input);
            if(multiple) {
                sendMessage(receivers, input);
            } else {
                if(e.getGuild().getTextChannelById(receiver) != null) {
                    sendMessage(e.getGuild().getTextChannelById(receiver), input);
                } else if(e.getJDA().getUserById(receiver) != null) {
                    sendMessage(e.getJDA().getUserById(receiver), input);
                } else if(e.getGuild().getMembersByName(receiver, true).size() > 0) {
                    sendMessage(e.getGuild().getMembersByName(receiver, true), input);
                }
                else {
                    e.getChannel().sendMessage(input).queue();
                }
            }
            if(!silent) {
                e.getChannel().sendMessage("I have sent that message.").queue();
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

    private void sendMessage(List<Member> users, final String input) {
        users.forEach(user -> sendMessage(user.getUser(), input));
    }

}
