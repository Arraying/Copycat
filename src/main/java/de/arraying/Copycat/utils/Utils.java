package de.arraying.Copycat.utils;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.data.DataSayValues;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.awt.*;

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
public class Utils {

    private static Utils instance;

    /**
     * Static instance getter.
     * @return  An instance of the object.
     */
    public static Utils getInstance() {
        if(instance == null) {
            instance = new Utils();
        }
        return instance;
    }

    /**
     * Makes a new Copycat style embed. Used to avoid repetition.
     * @return An instance of the Copycat custom embed builder.
     */
    public UtilsEmbedBuilder getCopycatBuilder() {
        UtilsEmbedBuilder embedBuilder = new UtilsEmbedBuilder();
        embedBuilder.setColor(new Color(34, 150, 245));
        embedBuilder.setAuthor("Copycat","https://discordapp.com", null);
        return embedBuilder;
    }

    /**
     * Gets the parameter value of given input string.
     * For example, "-parameter hello" would return hello,
     * if the parameter was set to -parameter.
     * @param input The string to take the value from.
     * @param parameter The parameter.
     * @return The parameter value, or "".
     */
    public String getParameterValue(String input, String parameter) {
        String substring = input.substring(input.indexOf(parameter)+parameter.length());
        int spaceIndex = substring.indexOf(" ");
        if(spaceIndex != -1) {
            substring = substring.substring(spaceIndex+1);
        }
        spaceIndex = substring.indexOf(" ");
        if(spaceIndex != -1) {
            substring = substring.substring(0, spaceIndex);
        }
        return substring;
    }

    /**
     * Strips all the formatting off a string.
     * @param string The string to strip.
     * @return A string without formatting.
     */
    public String stripFormatting(String string) {
        return string.replace("@here", "@\u180Ehere").replace("@everyone", "@\u180Eeveryone");
    }

    public boolean isInt(String input) {
        try {
            Integer.valueOf(input);
            return true;
        } catch(NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Sends a message using a queue.
     * @param channel The text channel the command was executed in.
     * @param data The say command data.
     * @param sender The command executor's user object.
     * @param message The message itself.
     */
    public void sendMessages(TextChannel channel, DataSayValues data, User sender, String message) {
        Copycat copycat = Copycat.getInstance();
        Guild guild = channel.getGuild();
        String id = guild.getId();
        if(copycat.getQueue().containsKey(id)) {
           channel.sendMessage("I appreciate it that you are trying to user me, but you currently still have a message sending. " +
                   "Use the queue command to check its status.").queue();
           return;
        }
        copycat.getQueue().put(id, (data.getUserReceivers().size()+data.getChannelReceivers().size()));
        data.getUserReceivers().forEach(userid -> guild.getJDA().getUserById(userid).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage(message+"\n\nSent from \""+guild.getName()+"\" by "+sender.getName()+"#"+sender.getDiscriminator()+".").queue(complete ->
                        checkDone(channel, data.isSilent()))));
        data.getChannelReceivers().forEach(channelid -> {
            TextChannel textChannel = guild.getJDA().getTextChannelById(channelid);
            String finalInput;
            if(!PermissionUtil.checkPermission(textChannel, guild.getMember(sender), Permission.MESSAGE_MENTION_EVERYONE)) {
                finalInput = stripFormatting(message);
            } else {
                finalInput = message;
            }
            textChannel.sendMessage(finalInput).queue(complete -> checkDone(channel, data.isSilent()));
        });
    }

    /**
     * Checks if all messages have been sent.
     * @param channel The channel the command was executed in.
     * @param silent Whether this message should execute silently.
     */
    private void checkDone(TextChannel channel, boolean silent) {
        Copycat copycat = Copycat.getInstance();
        String id = channel.getGuild().getId();
        if(!copycat.getQueue().containsKey(id)) {
            return;
        }
        int currentQueue = copycat.getQueue().get(id);
        if(currentQueue == 1) {
            copycat.getQueue().remove(id);
            if(!silent) {
                channel.sendMessage("I have sent the message.").queue();
            }
        } else {
            copycat.getQueue().put(id, currentQueue-1);
        }
    }

}
