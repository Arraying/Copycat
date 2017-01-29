package de.arraying.Copycat;

import de.arraying.Copycat.objects.ObjectConfig;
import de.arraying.Copycat.commands.Command;
import de.arraying.Copycat.listeners.ListenerChat;
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
import java.util.TreeMap;

public class Copycat {

    private static Copycat instance;
    @Getter private JDA jda;
    @Getter private TreeMap<String, Command> commands;
    @Getter private SimpleLog logger;
    @Getter private ObjectConfig config;

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
        String botAuthor = "115134128717955080";
        String botPrefix = "copycat ";
        String botVersion = "1.0.0";
        File configFile = new File("config.json");
        if(!configFile.exists()) {
            try {
                if(configFile.createNewFile()) {
                    String jsonString = "{\n"+
                            "\t\"botToken\":\""+botToken+"\",\n"+
                            "\t\"botAuthor\":\""+botAuthor+"\",\n"+
                            "\t\"botPrefix\":\""+botPrefix+"\",\n"+
                            "\t\"botVersion\":\""+botVersion+"\"\n"+
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
                botAuthor = (String) jsonObject.get("botAuthor");
                botPrefix = (String) jsonObject.get("botPrefix");
                botVersion = (String) jsonObject.get("botVersion");
                config = new ObjectConfig(botToken, botAuthor, botPrefix, botVersion);
            } catch(IOException | ParseException e) {
                logger.log(SimpleLog.Level.FATAL, "Could not parse config.json, shutting down.");
            }
        }
        commands = new TreeMap<>();
        Reflections reflections = new Reflections("de.arraying.Copycat");
        reflections.getSubTypesOf(Command.class).forEach(subclass -> {
            try {
                Command command = subclass.newInstance();
                commands.put(command.getName(), command);
                logger.log(SimpleLog.Level.INFO, "Registered the command "+command.getName()+".");
            } catch(InstantiationException | IllegalAccessException e) {
                logger.log(SimpleLog.Level.FATAL, "Could not register "+subclass.getSimpleName()+".");
            }
        });
        logger.log(SimpleLog.Level.INFO, "Loaded everything, it took "+(System.currentTimeMillis()-timeBegin)+" milliseconds. Starting JDA now.");
        try {
             jda = new JDABuilder(AccountType.BOT)
                    .setToken(botToken)
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setGame(Game.of(botPrefix+"help || "+botVersion, "https://twitch.tv/ "))
                    .addListener(new ListenerChat())
                    .buildBlocking();
        } catch(LoginException | InterruptedException | RateLimitedException e) {
            logger.log(SimpleLog.Level.FATAL, "The bot encountered an exception.");
            e.printStackTrace();
        }
    }

}
