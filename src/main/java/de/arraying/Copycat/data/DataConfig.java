package de.arraying.Copycat.data;

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
public @Data
class DataConfig {

	private String botToken;
	private String botBetaToken;
	private String botAuthor;
	private String botPrefix;
	private String botVersion;
	private boolean botBeta;
	private String mySQLHost;
	private String mySQLDatabase;
	private String mySQLUsername;
	private String mySQLPassword;
	private String mySQLGuildsTable;
	private String keyCarbonitex;
	private String keyBotsDiscordPw;
	private String keyDiscordBotsOrg;

	/**
	 * The local config objects file for the bot.
	 *
	 * @param botToken The token of the bot.
	 * @param botBetaToken The token of the beta bot.
	 * @param botAuthor The ID of the author of the bot.
	 * @param botPrefix The prefix the bot will use.
	 * @param botVersion The version of the bot.
	 * @param botBeta Whether or not the bot should use the beta token.
	 * @param mySQLHost The host name of the MySQL database.
	 * @param mySQLDatabase The database name of the MySQL database.
	 * @param mySQLUsername The username of the MySQL database.
	 * @param mySQLPassword The password of the MySQL database.
	 * @param mySQLGuildsTable The name of the table where guilds will be stored in.
	 * @param keyCarbonitex The Carbonitex API key.
	 * @param keyBotsDiscordPw The bots.discord.pw API key.
	 * @param keyDiscordBotsOrg The discordbots.org API key.
	 */
	public DataConfig(String botToken, String botBetaToken, String botAuthor, String botPrefix,
			String botVersion, boolean botBeta,
			String mySQLHost, String mySQLDatabase, String mySQLUsername, String mySQLPassword,
			String mySQLGuildsTable,
			String keyCarbonitex, String keyBotsDiscordPw, String keyDiscordBotsOrg) {
		this.botToken = botToken;
		this.botBetaToken = botBetaToken;
		this.botAuthor = botAuthor;
		this.botPrefix = botPrefix;
		this.botVersion = botVersion;
		this.botBeta = botBeta;
		this.mySQLHost = mySQLHost;
		this.mySQLDatabase = mySQLDatabase;
		this.mySQLUsername = mySQLUsername;
		this.mySQLPassword = mySQLPassword;
		this.mySQLGuildsTable = mySQLGuildsTable;
		this.keyCarbonitex = keyCarbonitex;
		this.keyBotsDiscordPw = keyBotsDiscordPw;
		this.keyDiscordBotsOrg = keyDiscordBotsOrg;
	}

	public DataConfig() {
	}

	public String getBotToken() {
		return this.botToken;
	}

	public String getBotBetaToken() {
		return botBetaToken;
	}

	public String getBotAuthor() {
		return botAuthor;
	}

	public String getBotPrefix() {
		return botPrefix;
	}

	public String getBotVersion() {
		return botVersion;
	}

	public boolean isBotBeta() {
		return botBeta;
	}

	public String getMySQLHost() {
		return mySQLHost;
	}

	public String getMySQLDatabase() {
		return mySQLDatabase;
	}

	public String getMySQLUsername() {
		return mySQLUsername;
	}

	public String getMySQLPassword() {
		return mySQLPassword;
	}

	public String getKeyCarbonitex() {
		return keyCarbonitex;
	}

	public String getMySQLGuildsTable() {
		return mySQLGuildsTable;
	}

	public String getKeyBotsDiscordPw() {
		return keyBotsDiscordPw;
	}

	public String getKeyDiscordBotsOrg() {
		return keyDiscordBotsOrg;
	}

}

