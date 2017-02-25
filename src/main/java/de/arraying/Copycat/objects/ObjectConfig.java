package de.arraying.Copycat.objects;

import lombok.Data;

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
public @Data class ObjectConfig {

    private final String botToken;
    private final String botBetaToken;
    private final String botAuthor;
    private final String botPrefix;
    private final String botVersion;
    private final boolean botBeta;
    private final String keyCarbonitex;
    private final String keyBotsDiscordPw;

    /**
     * The local config objects file for the bot.
     * @param botToken The token of the bot.
     * @param botBetaToken The token of the beta bot.
     * @param botAuthor The ID of the author of the bot.
     * @param botPrefix The prefix the bot will use.
     * @param botVersion The version of the bot.
     * @param botBeta Whether or not the bot should use the beta token.
     * @param keyCarbonitex The Carbonitex API key.
     * @param keyBotsDiscordPw The bots.discord.pw API key.
     */
    public ObjectConfig(String botToken, String botBetaToken, String botAuthor, String botPrefix, String botVersion, boolean botBeta,
                        String keyCarbonitex, String keyBotsDiscordPw) {
        this.botToken = botToken;
        this.botBetaToken = botBetaToken;
        this.botAuthor = botAuthor;
        this.botPrefix = botPrefix;
        this.botVersion = botVersion;
        this.botBeta = botBeta;
        this.keyCarbonitex = keyCarbonitex;
        this.keyBotsDiscordPw = keyBotsDiscordPw;
    }

}
