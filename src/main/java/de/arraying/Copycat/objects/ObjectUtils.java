package de.arraying.Copycat.objects;

import java.awt.*;

public class ObjectUtils {

    private static ObjectUtils instance;

    /**
     * Static instance getter.
     * @return  An instance of the object.
     */
    public static ObjectUtils getInstance() {
        if(instance == null) {
            instance = new ObjectUtils();
        }
        return instance;
    }

    /**
     * Makes a new Copycat style embed. Used to avoid repetition.
     * @return An instance of the Copycat custom embed builder.
     */
    public ObjectEmbedBuilder getCopycatBuilder() {
        ObjectEmbedBuilder embedBuilder = new ObjectEmbedBuilder();
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
        return string.replace("*", "\\*")
                .replace("`", "\\`")
                .replace("_", "\\_")
                .replace("~~", "\\~\\~")
                .replace(">", "\\>")
                .replace("@", "@\u180E");
    }

}
