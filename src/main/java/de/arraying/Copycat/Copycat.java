package de.arraying.Copycat;

import de.arraying.Copycat.listeners.ListenerChange;
import de.arraying.Copycat.objects.ObjectConfig;
import de.arraying.Copycat.commands.Command;
import de.arraying.Copycat.listeners.ListenerChat;
import de.arraying.Copycat.objects.ObjectRequester;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.reflections.Reflections;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.HashMap;
import java.util.TreeMap;

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
public class Copycat {

    private static Copycat instance;
    @Getter private JDA jda;
    @Getter private TreeMap<String, Command> commands;
    @Getter private HashMap<String, Integer> queue;
    @Getter private SimpleLog logger;
    @Getter private ObjectConfig config;
    @Getter private ObjectRequester requester;

    /**
     * Static instance getter.
     * @return  An instance of the object.
     */
    public static Copycat getInstance() {
        if(instance == null) {
            instance = new Copycat();
        }
        return instance;
    }

    public void run() {
        long timeBegin = System.currentTimeMillis();
        logger = SimpleLog.getLog("Copycat");
        logger.log(SimpleLog.Level.INFO, "Copycat is now starting.");
        String botToken = "none";
        String botBetaToken = "none";
        String botAuthor = "115134128717955080";
        String botPrefix = "copycat ";
        String botVersion = "1.0.0";
        boolean botBeta = true;
        String keyCarbonitex = "arraying123456789";
        String keyBotsDiscordPw = "abcdefghijklmnopqrstuvwxyz123456789";
        File configFile = new File("config.json");
        if(!configFile.exists()) {
            try {
                if(configFile.createNewFile()) {
                    String jsonString = "{\n"+
                            "\t\"botToken\":\""+botToken+"\",\n"+
                            "\t\"botBetaToken\":\""+botBetaToken+"\",\n"+
                            "\t\"botAuthor\":\""+botAuthor+"\",\n"+
                            "\t\"botPrefix\":\""+botPrefix+"\",\n"+
                            "\t\"botVersion\":\""+botVersion+"\",\n"+
                            "\t\"botBeta\":"+botBeta+",\n"+
                            "\t\"keyCarbonitex\":\""+keyCarbonitex+"\",\n"+
                            "\t\"keyBotsDiscordPw\":\""+keyBotsDiscordPw+"\"\n"+
                            "}";
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile));
                    bufferedWriter.write(jsonString);
                    bufferedWriter.close();
                    logger.log(SimpleLog.Level.INFO, "Please fill in the config.json information.");
                } else {
                    logger.log(SimpleLog.Level.FATAL, "Could not create config.json.");
                }
                System.exit(0);
            } catch(Exception e) {
                logger.log(SimpleLog.Level.FATAL, "Could not create config.json.");
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            try {
                JSONParser jsonParser = new JSONParser();
                Object json = jsonParser.parse(new FileReader(configFile));
                JSONObject jsonObject = (JSONObject) json;
                botToken = (String) jsonObject.get("botToken");
                botBetaToken = (String) jsonObject.get("botBetaToken");
                botAuthor = (String) jsonObject.get("botAuthor");
                botPrefix = (String) jsonObject.get("botPrefix");
                botVersion = (String) jsonObject.get("botVersion");
                botBeta = (boolean) jsonObject.get("botBeta");
                keyCarbonitex = (String) jsonObject.get("keyCarbonitex");
                keyBotsDiscordPw = (String) jsonObject.get("keyBotsDiscordPw");
                config = new ObjectConfig(botToken, botBetaToken, botAuthor, botPrefix, botVersion, botBeta, keyCarbonitex, keyBotsDiscordPw);
            } catch(IOException | ParseException e) {
                logger.log(SimpleLog.Level.FATAL, "Could not parse config.json, shutting down.");
            }
        }
        commands = new TreeMap<>();
        Reflections reflections = new Reflections("de.arraying.Copycat.commands");
        reflections.getSubTypesOf(Command.class).forEach(subclass -> {
            try {
                Command command = subclass.newInstance();
                commands.put(command.getName(), command);
                logger.log(SimpleLog.Level.INFO, "Registered the command "+command.getName()+".");
            } catch(InstantiationException | IllegalAccessException e) {
                logger.log(SimpleLog.Level.FATAL, "Could not register "+subclass.getSimpleName()+".");
            }
        });
        queue = new HashMap<>();
        requester = new ObjectRequester();
        logger.log(SimpleLog.Level.INFO, "Loaded everything, it took "+(System.currentTimeMillis()-timeBegin)+" milliseconds. Starting JDA now.");
        try {
             jda = new JDABuilder(AccountType.BOT)
                    .setToken(botBeta ? botBetaToken : botToken)
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setGame(Game.of(botPrefix+"help || "+botVersion))
                    .addListener(new ListenerChat())
                     .addListener(new ListenerChange())
                    .buildBlocking();
        } catch(LoginException | InterruptedException | RateLimitedException e) {
            logger.log(SimpleLog.Level.FATAL, "The bot encountered an exception.");
            e.printStackTrace();
        }
    }

}
