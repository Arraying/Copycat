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
            long before = System.currentTimeMillis();
            String input = e.getMessage().getRawContent().substring(copycat.getConfig().getBotPrefix().length());
            input = input.substring(input.indexOf(" "));
            String receiver = "nutin";
            List<Member> userReceivers = new ArrayList<>();
            List<TextChannel> channelReceivers = new ArrayList<>();
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
                if(!e.getMessage().getMentionedChannels().isEmpty()) {
                    multiple = true;
                    e.getMessage().getMentionedChannels().forEach(channel -> {
                        if(PermissionUtil.checkPermission(channel, e.getMember(), Permission.MESSAGE_WRITE)) {
                            channelReceivers.add(channel);
                        }
                    });
                }
                input = input.replace("-c", "");
                for(TextChannel textChannel : e.getMessage().getMentionedChannels()) {
                    input = input.replace("<#"+textChannel.getId()+">", "");
                }
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
                    channelReceivers.clear();
                    e.getMessage().getMentionedUsers().forEach(user -> userReceivers.add(e.getGuild().getMember(user)));
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
                if(channelReceivers.isEmpty()) {
                    sendMessage(userReceivers, input);
                } else {
                    sendMessageChannel(channelReceivers, input);
                }
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

    private void sendMessageChannel(List<TextChannel> channels, final String input) {
        channels.forEach(channel -> channel.sendMessage(input).queue());
    }

}
