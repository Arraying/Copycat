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
import java.util.Random;

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
            input = input.trim().replaceAll(" +", " ");
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
                if(!silent) {
                    e.getChannel().sendMessage("I have sent the message.").queue();
                }
            } else {
                utils.sendMessages(e.getChannel(), userReceivers, channelReceivers, e.getAuthor(), input, silent);
            }
        } else {
            e.getChannel().sendMessage(getSyntaxMessage()).queue();
        }
    }

}
