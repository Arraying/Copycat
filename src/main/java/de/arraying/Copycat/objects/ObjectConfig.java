package de.arraying.Copycat.objects;

import lombok.Data;

public @Data class ObjectConfig {

    private final String botToken;
    private final String botAuthor;
    private final String botPrefix;
    private final String botVersion;

    /**
     * The local config objects file for the bot.
     * @param botToken The token of the bot.
     * @param botAuthor The ID of the author of the bot.
     * @param botPrefix The prefix the bot will use.
     * @param botVersion The version of the bot.
     */
    public ObjectConfig(String botToken, String botAuthor, String botPrefix, String botVersion) {
        this.botToken = botToken;
        this.botAuthor = botAuthor;
        this.botPrefix = botPrefix;
        this.botVersion = botVersion;
    }

}
