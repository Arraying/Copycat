package de.arraying.Copycat.objects;

import lombok.Data;

public @Data class ObjectConfig {

    private final String botToken;
    private final String botBetaToken;
    private final String botAuthor;
    private final String botPrefix;
    private final String botVersion;
    private final boolean botBeta;

    /**
     * The local config objects file for the bot.
     * @param botToken The token of the bot.
     * @param botBetaToken The token of the beta bot.
     * @param botAuthor The ID of the author of the bot.
     * @param botPrefix The prefix the bot will use.
     * @param botVersion The version of the bot.
     * @param botBeta Whether or not the bot should use the beta token.
     */
    public ObjectConfig(String botToken, String botBetaToken, String botAuthor, String botPrefix, String botVersion, boolean botBeta) {
        this.botToken = botToken;
        this.botBetaToken = botBetaToken;
        this.botAuthor = botAuthor;
        this.botPrefix = botPrefix;
        this.botVersion = botVersion;
        this.botBeta = botBeta;
    }

}
