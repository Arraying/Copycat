package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.objects.ObjectUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
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
    private Random randomObject;

    public CommandSay() {
        super("say", "My main feature. For more information, check my docs.", Permission.MESSAGE_WRITE, "say <input>", false);
        copycat = Copycat.getInstance();
        utils = ObjectUtils.getInstance();
        randomObject = new Random();
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent e, String[] args) {
        if(args.length > 1) {
            String input = e.getMessage().getRawContent().substring(copycat.getConfig().getBotPrefix().length());
            input = input.substring(input.indexOf(" "));
            boolean silent = false;
            boolean random = false;
            ArrayList<String> userReceivers = new ArrayList<>();
            ArrayList<String> channelReceivers = new ArrayList<>();
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
                            && PermissionUtil.checkPermission(textChannel, e.getMember(), Permission.MESSAGE_WRITE)
                            && !channelReceivers.contains(textChannel.getId())) {
                        channelReceivers.add(textChannel.getId());
                    }
                }
                input = input.replace("-cid "+value, "");
            }
            if(input.contains("-c")) {
                if(!e.getMessage().getMentionedChannels().isEmpty()) {
                    e.getMessage().getMentionedChannels().forEach(channel -> {
                        if(PermissionUtil.checkPermission(channel, e.getMember(), Permission.MESSAGE_WRITE)
                                && !channelReceivers.contains(channel.getId())) {
                            channelReceivers.add(channel.getId());
                        }
                    });
                }
                input = input.replace("-c", "");
                for(TextChannel textChannel : e.getMessage().getMentionedChannels()) {
                    input = input.replace("<#"+textChannel.getId()+">", "");
                }
            }
            if(input.contains("-pmid")) {
                String value = utils.getParameterValue(input, "-pmid");
                if(!value.equalsIgnoreCase("")
                        && PermissionUtil.checkPermission(e.getChannel(), e.getMember(), Permission.MESSAGE_MENTION_EVERYONE)) {
                    User user = e.getGuild().getMemberById(value).getUser();
                    if(user != null
                            && !userReceivers.contains(user.getId())) {
                        userReceivers.add(user.getId());
                    }
                }
                input = input.replace("-pmid "+value, "");
            }
            if(input.contains("-pm")) {
                if(!e.getMessage().getMentionedUsers().isEmpty()
                        && PermissionUtil.checkPermission(e.getChannel(), e.getMember(), Permission.MESSAGE_MENTION_EVERYONE)) {
                    e.getMessage().getMentionedUsers().forEach(user -> {
                        if(!userReceivers.contains(user.getId())) {
                            userReceivers.add(user.getId());
                        }
                    });
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
            if(channelReceivers.isEmpty()
                    && userReceivers.isEmpty()) {
                if(!PermissionUtil.checkPermission(e.getChannel(), e.getMember(), Permission.MESSAGE_MENTION_EVERYONE)) {
                    input = utils.stripFormatting(input);
                }
                e.getChannel().sendMessage(input).queue();
            } else {
                sendMessageChannel(channelReceivers, e.getGuild(), e.getAuthor(), input);
                sendMessageMember(userReceivers, e.getGuild(), e.getAuthor(), input);
            }
            if(!silent) {
                e.getChannel().sendMessage("I have sent that message.").queue();
            }
        } else {
            e.getChannel().sendMessage(getSyntaxMessage()).queue();
        }
    }

    private void sendMessageMember(List<String> users, Guild guild, User sender, String input) {
        users.forEach(userid -> guild.getJDA().getUserById(userid).openPrivateChannel().queue(channel ->
                channel.sendMessage(input+"\n\nSent from \""+guild.getName()+"\" by "+sender.getName()+"#"+sender.getDiscriminator()+".").queue()));
    }

    private void sendMessageChannel(List<String> channels, Guild guild, User sender, String input) {
        channels.forEach(channelid -> {
            TextChannel textChannel = guild.getJDA().getTextChannelById(channelid);
            String finalInput;
            if(!PermissionUtil.checkPermission(textChannel, guild.getMember(sender), Permission.MESSAGE_MENTION_EVERYONE)) {
                finalInput = utils.stripFormatting(input);
            } else {
                finalInput = input;
            }
            textChannel.sendMessage(finalInput).queue();
        });

    }

}
