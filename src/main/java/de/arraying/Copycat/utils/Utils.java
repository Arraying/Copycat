package de.arraying.Copycat.utils;

import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.Messages;
import de.arraying.Copycat.data.DataSayValues;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
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
     * Replaces all the placeholders.
     * @param input The input string.
     * @param user The member executing the command.
     * @return A replaced string.
     */
    public String replacePlaceholders(String input, Member user) {
        return input.replace("{user}", user.getAsMention())
                .replace("{userid}", user.getUser().getId())
                .replace("{username}", user.getUser().getName())
                .replace("{userdiscriminator}", user.getUser().getDiscriminator())
                .replace("{guildcount}", String.valueOf(user.getGuild().getMembers().size()))
                .replace("{guildname}", user.getGuild().getName())
                .replace("{guildid}", user.getGuild().getId())
                .replace("{random}", String.valueOf(new Random().nextInt(Short.MAX_VALUE-1)+1))
                .replace("{date}", getDate())
                .replace("{newline}", "\n");
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
           channel.sendMessage(Messages.get(guild, "message.queue")).queue();
           return;
        }
        copycat.getQueue().put(id, (data.getUserReceivers().size()+data.getChannelReceivers().size()));
        String tempFooter = Copycat.getInstance().getLocalGuilds().get(guild.getId()).getFooter();
        String nearlyFooter = tempFooter == null ?  Messages.get(guild, "message.footer") : tempFooter;
        String footer = replacePlaceholders(nearlyFooter, guild.getMember(sender));
        data.getUserReceivers().forEach(userid -> guild.getJDA().getUserById(userid).openPrivateChannel().queue(privateChannel ->
                privateChannel.sendMessage(message+"\n"+footer).queue(complete ->
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
                channel.sendMessage(Messages.get(channel.getGuild(), "command.say.sent")).queue();
            }
        } else {
            copycat.getQueue().put(id, currentQueue-1);
        }
    }

    /**
     * Gets the date in a custom format.
     * @return A date and time string.
     */
    private String getDate() {
        String dateFormat = "dd/MM/yyyy kk:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(new Date());
    }

}
