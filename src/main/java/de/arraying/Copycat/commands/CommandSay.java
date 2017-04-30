package de.arraying.Copycat.commands;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.Messages;
import de.arraying.Copycat.data.DataSay;
import de.arraying.Copycat.data.DataSayValues;
import de.arraying.Copycat.parameters.Parameter;
import de.arraying.Copycat.utils.Utils;
import net.dv8tion.jda.core.Permission;
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
     */
    public CommandSay() {
        super("say", "command.say.description", Permission.MESSAGE_WRITE, "say <input>", false);
        copycat = Copycat.getInstance();
        utils = Utils.getInstance();
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent e, String[] args) {
        if(args.length > 1) {
            String input = e.getMessage().getRawContent().substring(copycat.getDataConfig().getBotPrefix().length());
            DataSay.start(e.getMessage().getId());
            input = input.substring(input.indexOf(" "));
            for(Parameter parameter : copycat.getParameters()) {
                while(input.contains(parameter.getTrigger())) {
                    input = parameter.invoke(e, input);
                }
            }
            input = input.trim().replaceAll(" +", " ");
            if(input.isEmpty()
                    || input.equalsIgnoreCase(" ")) {
                e.getChannel().sendMessage(Messages.get(e.getGuild(), "command.say.empty")).queue();
                return;
            }
            DataSayValues data = DataSay.retrieve(e.getMessage().getId());
            if(data.isDelete()
                    && PermissionUtil.checkPermission(e.getChannel(), e.getGuild().getSelfMember(), Permission.MESSAGE_MANAGE)) {
                e.getMessage().delete().queue();
            }
            final String finalInput = utils.replacePlaceholders(input, e.getMember());
            if(data.getDelay() != -1) {
                copycat.getScheduler().schedule(() -> handleSend(e, data, finalInput), data.getDelay(), TimeUnit.SECONDS);
            } else {
                handleSend(e, data, finalInput);
            }
        } else {
            e.getChannel().sendMessage(getSyntaxMessage(e.getGuild())).queue();
        }
    }

    /**
     * Handles the message and sends it.
     * @param e The event.
     * @param input The message to send.
     */
    private void handleSend(GuildMessageReceivedEvent e, DataSayValues data, String input) {
        if(data.getChannelReceivers().isEmpty()
                && data.getUserReceivers().isEmpty()) {
            if(!PermissionUtil.checkPermission(e.getChannel(), e.getMember(), Permission.MESSAGE_MENTION_EVERYONE)) {
                input = utils.stripFormatting(input);
            }
            e.getChannel().sendMessage(input).queue();
            if(!data.isSilent()) {
                e.getChannel().sendMessage(Messages.get(e.getGuild(), "command.say.sent")).queue();
            }
        } else {
            utils.sendMessages(e.getChannel(), data, e.getAuthor(), input);
        }
        DataSay.complete(e.getMessage().getId());
    }

}
