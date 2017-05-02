package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.Messages;
import de.arraying.Copycat.data.DataSay;
import de.arraying.Copycat.data.DataSayValues;
import de.arraying.Copycat.parameters.Parameter;
import de.arraying.Copycat.utils.Utils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.concurrent.TimeUnit;

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

    private final Copycat copycat;
    private final Utils utils;

    /**
     * Readies the say command.
     * The say command repeats the given phrase, however
     * there are many parameters to choose from, enabling it
     * to hook into bots and execute tasks that are not yet
     * possible with other bots.
     */
    public CommandSay() {
        super("say", "command.say.description", Permission.MESSAGE_WRITE, "say <input>", false);
        copycat = Copycat.getInstance();
        utils = Utils.getInstance();
    }

    /**
     * Invokes the say command. First, it handles general placeholders,
     * then it handles the input, then it sends it to the utils
     * in order to be sent.
     * @param event The message event. Contains all required objects.
     * @param args The arguments, including the command itself.
     */
    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        Message message = event.getMessage();
        Member member = event.getMember();
        if(args.length > 1) {
            StringBuilder inputBuilder = new StringBuilder();
            for(int i = 1; i < args.length; i++) {
                inputBuilder.append(args[i]).append(" ");
            }
            String input = inputBuilder.toString().trim();
            DataSay.start(message.getId());
            for(Parameter parameter : copycat.getParameters()) {
                while(input.contains(parameter.getTrigger())) {
                    input = parameter.invoke(event, input);
                }
            }
            input = input.trim().replaceAll(" +", " ");
            if(input.isEmpty()
                    || input.equalsIgnoreCase(" ")) {
                channel.sendMessage(Messages.get(guild, "command.say.empty")).queue();
                return;
            }
            DataSayValues data = DataSay.retrieve(message.getId());
            if(data.isDelete()
                    && PermissionUtil.checkPermission(channel, guild.getSelfMember(), Permission.MESSAGE_MANAGE)) {
                message.delete().queue();
            }
            final String finalInput = utils.replacePlaceholders(input, member);
            if(data.getDelay() != -1) {
                copycat.getScheduler().schedule(() -> handleSend(event, data, finalInput), data.getDelay(), TimeUnit.SECONDS);
            } else {
                handleSend(event, data, finalInput);
            }
        } else {
            channel.sendMessage(getSyntaxMessage(guild)).queue();
        }
    }

    /**
     * Handles the message before it sending
     * it to the utils to be sent.
     * @param event The message event, inherited from the invoke.
     * @param input The message to send.
     */
    private void handleSend(GuildMessageReceivedEvent event, DataSayValues data, String input) {
        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        Member member = event.getMember();
        if(data.getChannelReceivers().isEmpty()
                && data.getUserReceivers().isEmpty()) {
            if(!PermissionUtil.checkPermission(channel, member, Permission.MESSAGE_MENTION_EVERYONE)) {
                input = utils.stripFormatting(input);
            }
            channel.sendMessage(input).queue();
            if(!data.isSilent()) {
                channel.sendMessage(Messages.get(guild, "command.say.sent")).queue();
            }
        } else {
            utils.sendMessages(channel, data, member.getUser(), input);
        }
        DataSay.complete(event.getMessage().getId());
    }

}
