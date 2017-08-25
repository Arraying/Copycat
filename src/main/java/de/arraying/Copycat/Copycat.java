package de.arraying.Copycat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.arraying.Copycat.commands.Command;
import de.arraying.Copycat.data.DataConfig;
import de.arraying.Copycat.data.DataGuild;
import de.arraying.Copycat.data.DataManager;
import de.arraying.Copycat.data.DataSay;
import de.arraying.Copycat.listeners.ListenerChange;
import de.arraying.Copycat.listeners.ListenerChat;
import de.arraying.Copycat.parameters.Parameter;
import de.arraying.Copycat.utils.UtilsJson;
import de.arraying.Copycat.utils.UtilsRequester;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.security.auth.login.LoginException;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.reflections.Reflections;

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
	@Getter	private JDA jda;
	@Getter	private TreeMap<String, Command> commands;
	@Getter	private HashMap<String, DataGuild> localGuilds;
	@Getter	private HashMap<String, Integer> queue;
	@Getter	private ArrayList<Parameter> parameters;
	@Getter	private static SimpleLog logger;
	@Getter	private static DataConfig dataConfig;
	@Getter	private DataSay dataSay;
	@Getter	private DataManager dataManager;
	@Getter	private UtilsRequester requester;
	@Getter	private ScheduledExecutorService scheduler;
	@Getter	private static Gson save;
	@Getter	private static Gson load;

	/**
	 * Static instance getter.
	 *
	 * @return An instance of the object.
	 */
	public static Copycat getInstance() {
		if(instance == null) {
			instance = new Copycat();
		}
		return instance;
	}

	/**
	 * Static gson object for loading
	 *
	 * @return The object
	 */
	public static Gson getGsonLoad() {
		return load;
	}

	/**
	 * Static gson object for saving
	 *
	 * @return The object
	 */
	public static Gson getGsonSave() {
		return save;
	}

	/**
	 * Static logger object for logging
	 *
	 * @return The object
	 */
	public static SimpleLog getLogger() {
		return logger;
	}

	/**
	 * Static config object for info
	 *
	 * @return The object
	 */
	public static DataConfig getConfig() {
		return dataConfig;
	}

	void run() {
		long timeBegin = System.currentTimeMillis();
		save = new GsonBuilder().setPrettyPrinting().create();
		load = new Gson();
		logger = SimpleLog.getLog("Copycat");
		logger.log(SimpleLog.Level.INFO, "Copycat is now starting.");
		String botToken = "none";
		String botBetaToken = "none";
		String botAuthor = "115134128717955080";
		String botPrefix = "copycat ";
		String botVersion = "1.0.0";
		boolean botBeta = true;
		String mySQLHost = "FILL_IN";
		String mySQLDatabase = "Arraybot";
		String mySQLUsername = "Arraybot";
		String mySQLPassword = "FILL_IN";
		String mySQLGuildsTable = "cc_guilds";
		String keyCarbonitex = "arraying123456789";
		String keyBotsDiscordPw = "abcdefghijklmnopqrstuvwxyz123456789";
		String keyDiscordBotsOrg = "qwertyuiop1234567890";
		File configFile = new File("config.json");
		if(!configFile.exists()) {
			try {
				if(configFile.createNewFile()) {
					dataConfig = new DataConfig(botToken, botBetaToken, botAuthor, botPrefix, botVersion,
							botBeta, mySQLHost, mySQLDatabase, mySQLUsername, mySQLPassword, mySQLGuildsTable,
							keyCarbonitex, keyBotsDiscordPw, keyDiscordBotsOrg);
					UtilsJson.saveConfig(configFile);
					logger.log(SimpleLog.Level.INFO, "Please fill in the config.json information.");
				}
				System.exit(0);
			} catch(Exception e) {
				logger.log(SimpleLog.Level.FATAL, "Could not create config.json.");
				e.printStackTrace();
				System.exit(1);
			}
		} else {
			dataConfig = UtilsJson.loadConfig(configFile);
		}
		logger.log(SimpleLog.Level.INFO, "Caching all the guilds...");
		localGuilds = new HashMap<>();
		try {
			dataManager = new DataManager();
			dataManager.cacheGuilds();
		} catch(IllegalStateException exception) {
			logger.log(SimpleLog.Level.FATAL,
					"An exception occurred starting the database manager: " + exception.getMessage());
			return;
		}
		logger.log(SimpleLog.Level.INFO, "Loading in all the languages...");
		try {
			Messages.init();
		} catch(IllegalStateException exception) {
			logger.log(SimpleLog.Level.FATAL, "Unable to start the bot due to a language error.");
			return;
		}
		logger.log(SimpleLog.Level.INFO, "Registering all commands...");
		commands = new TreeMap<>();
		Reflections reflections = new Reflections("de.arraying.Copycat.commands");
		reflections.getSubTypesOf(Command.class).forEach(subclass -> {
			try {
				Command command = subclass.newInstance();
				commands.put(command.getName(), command);
				logger.log(SimpleLog.Level.INFO, "Registered the command \"" + command.getName() + "\".");
			} catch(InstantiationException | IllegalAccessException exception) {
				logger
						.log(SimpleLog.Level.FATAL, "Could not register \"" + subclass.getSimpleName() + "\".");
			}
		});
		parameters = new ArrayList<>();
		reflections = new Reflections("de.arraying.Copycat.parameters");
		reflections.getSubTypesOf(Parameter.class).forEach(subclass -> {
			try {
				Parameter parameter = subclass.newInstance();
				parameters.add(parameter);
				logger.log(SimpleLog.Level.INFO,
						"Registered the parameter \"" + parameter.getTrigger() + "\"");
			} catch(InstantiationException | IllegalAccessException exception) {
				logger
						.log(SimpleLog.Level.FATAL, "Could not register \"" + subclass.getSimpleName() + "\".");
			}
		});
		queue = new HashMap<>();
		dataSay = new DataSay();
		requester = new UtilsRequester();
		scheduler = Executors.newScheduledThreadPool(1);
		logger.log(SimpleLog.Level.INFO,
				"Loaded everything, it took " + (System.currentTimeMillis() - timeBegin)
						+ " milliseconds. Starting JDA now.");
		try {
			jda = new JDABuilder(AccountType.BOT)
					.setToken(botBeta ? botBetaToken : botToken)
					.setStatus(OnlineStatus.DO_NOT_DISTURB)
					.setGame(Game.of(botPrefix + "help || " + botVersion))
					.addEventListener(new ListenerChat())
					.addEventListener(new ListenerChange())
					.buildBlocking();
		} catch(LoginException | InterruptedException | RateLimitedException exception) {
			logger.log(SimpleLog.Level.FATAL,
					"The bot encountered an exception: " + exception.getMessage());
			return;
		}
	}

}
