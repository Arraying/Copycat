package de.arraying.Copycat.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.arraying.Copycat.Copycat;
import de.arraying.Copycat.Messages;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
public class DataManager {

    private final Copycat copycat;
    private final HikariDataSource dataSource;
    private final String guildsTableName;

    /**
     * Creates a new data manager object.
     */
    public DataManager() {
        try {
            this.copycat = Copycat.getInstance();
            this.guildsTableName = copycat.getDataConfig().getMySQLGuildsTable();
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://"+copycat.getDataConfig().getMySQLHost()+":3306/"+copycat.getDataConfig().getMySQLDatabase()+"?useSSL=false");
            config.setUsername(copycat.getDataConfig().getMySQLUsername());
            config.setPassword(copycat.getDataConfig().getMySQLPassword());
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(3);
            config.setLeakDetectionThreshold(3000);
            this.dataSource = new HikariDataSource(config);
            readyDatabase();
        } catch(Exception exception) {
            throw new IllegalStateException("The database could not be initialised due to the following exception: "+exception.getMessage());
        }
    }

    /**
     * Caches all the guilds into the bot.
     */
    public void cacheGuilds() {
        try {
            PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement("SELECT * FROM `"+guildsTableName+"`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                String id = resultSet.getString("id");
                String prefix = resultSet.getString("prefix");
                String language = resultSet.getString("language");
                String footer = resultSet.getString("footer");
                copycat.getLocalGuilds().put(id, new DataGuild(id, prefix, language, footer));
            }
            preparedStatement.getConnection().close();
            copycat.getLogger().log(SimpleLog.Level.INFO, "Caching complete, loaded in " +
                    copycat.getLocalGuilds().size() + " guilds.");
        } catch(SQLException exception) {
            copycat.getLogger().log(SimpleLog.Level.FATAL, "An error occurred caching in the guilds: "+exception.getMessage());
        }
    }

    /**
     * Adds a guild to the database.
     * @param id The ID of the guild.
     */
    public void addGuild(String id) {
        if(copycat.getLocalGuilds().containsKey(id)) {
            return;
        }
        try {
            PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement("INSERT INTO `"+guildsTableName+"`(" +
                    "`id`, `prefix`" +
                    ") VALUES ( " +
                    "?, ?" +
                    ");");
            String prefix = copycat.getDataConfig().getBotPrefix();
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, prefix);
            preparedStatement.executeUpdate();
            preparedStatement.getConnection().close();
            copycat.getLocalGuilds().put(id, new DataGuild(id, prefix, null, null));
            copycat.getLogger().log(SimpleLog.Level.INFO, "The guild "+id+" has been added.");
        } catch(SQLException exception) {
            copycat.getLogger().log(SimpleLog.Level.FATAL, "An error occurred adding the guild "+id+": "+exception.getMessage());
        }
    }

    /**
     * Removes a guild from the database.
     * @param id The ID of the guild.
     */
    public void removeGuild(String id) {
        if(!copycat.getLocalGuilds().containsKey(id)) {
            return;
        }
        try {
            PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement("DELETE FROM `"+guildsTableName+"` WHERE `id`=?");
            preparedStatement.setString(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.getConnection().close();
            copycat.getLocalGuilds().remove(id);
            copycat.getLogger().log(SimpleLog.Level.INFO, "The guild "+id+" has been removed.");
        } catch(SQLException exception) {
            copycat.getLogger().log(SimpleLog.Level.FATAL, "An error occurred removing the guild "+id+": "+exception.getMessage());
        }
    }

    /**
     * Checks if the guild is synced with the database.
     * @param e The guild message event.
     * @return True if the guild is synced, false otherwise.
     */
    public boolean checkSync(GenericGuildMessageEvent e) {
        if(!copycat.getLocalGuilds().containsKey(e.getGuild().getId())) {
            e.getChannel().sendMessage("You were not synced with my database, I have changed that.").queue();
            copycat.getDataManager().addGuild(e.getGuild().getId());
            return false;
        }
        return true;
    }

    /**
     * Executes an update in the guilds table.
     * @param field The name of the field to update.
     * @param guild The guild ID to update.
     * @param value  The new value of the field.
     */
    public void setGuildValue(String field, String guild, Object value) {
        if(!copycat.getLocalGuilds().containsKey(guild)) {
            return;
        }
        try {
            PreparedStatement preparedStatement =
                    dataSource.getConnection().prepareStatement("UPDATE "+guildsTableName+" SET `"+field+"`=? WHERE `id`=?");
            preparedStatement.setString(2, guild);
            preparedStatement.setObject(1, value);
            preparedStatement.executeUpdate();
            preparedStatement.getConnection().close();
            Field guildDataField = copycat.getLocalGuilds().get(guild).getClass().getDeclaredField(field);
            guildDataField.setAccessible(true);
            guildDataField.set(copycat.getLocalGuilds().get(guild), value);
        } catch(SQLException | IllegalAccessException | NoSuchFieldException exception) {
            copycat.getLogger().log(SimpleLog.Level.FATAL, "Could not update the guild value: "+exception.getMessage());
        }
    }

    /**
     * Readies the database.
     * @throws SQLException If an error occurs.
     */
    private void readyDatabase()
            throws SQLException {
        PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `"+guildsTableName+"`(" +
                "`id` BIGINT, " +
                "`prefix` TEXT, " +
                "`language` TEXT, " +
                "`footer` TEXT, " +
                "PRIMARY KEY(id)" +
                ");");
        preparedStatement.executeUpdate();
        preparedStatement.getConnection().close();
    }

}
