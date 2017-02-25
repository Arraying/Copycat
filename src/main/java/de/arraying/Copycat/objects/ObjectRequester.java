package de.arraying.Copycat.objects;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import de.arraying.Copycat.Copycat;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

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
public class ObjectRequester {

    private final Copycat copycat;

    /**
     * Constructor to get the copycat instance
     * for ease of access.
     */
    public ObjectRequester() {
        this.copycat = Copycat.getInstance();
        RequestConfig config = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
        HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(config).build();
        Unirest.setHttpClient(httpClient);
    }

    /**
     * Sends a POST request to both bots.discord.pw and Carbonitex.
     * @param botId The ID of the bot.
     * @param servercount The amount of guilds at the moment.
     * @throws UnirestException If the requests throw an error.
     */
    public void sendPostRequests(String botId, int servercount)
            throws UnirestException {
        if(!copycat.getConfig().isBotBeta()) {
            String carbonitex = copycat.getConfig().getKeyCarbonitex();
            String discordBots = copycat.getConfig().getKeyBotsDiscordPw();
            Unirest.post("https://www.carbonitex.net/discord/data/botdata.php")
                    .header("Content-Type", "application/json")
                    .body(new JSONObject().put("key", carbonitex).put("servercount", servercount))
                    .asString();
            Unirest.post("https://bots.discord.pw/api/bots/"+botId+"/stats")
                    .header("Authorization", discordBots)
                    .header("Content-Type", "application/json")
                    .body(new JSONObject().put("server_count", servercount))
                    .asString();
        }
    }

}
