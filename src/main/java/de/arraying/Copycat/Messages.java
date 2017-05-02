package de.arraying.Copycat;

import de.arraying.Copycat.data.DataGuild;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class Messages {

    private static final HashMap<String, ResourceBundle> LANGUAGES = new HashMap<>();
    private static final Copycat COPYCAT = Copycat.getInstance();
    private static final Pattern FILE_NAME = Pattern.compile("^Language_[a-z]{2,4}_[A-Z]{2,4}\\.properties$");

    /**
     * Initialises the messages.
     */
    static void init() {
        try {
            File dir = new File("locales");
            if(!dir.exists()) {
                COPYCAT.getLogger().log(SimpleLog.Level.FATAL, "The \"locales\" folder is not present.");
                return;
            }
            URL[] urls = {dir.toURI().toURL()};
            ClassLoader loader = new URLClassLoader(urls);
            for(File file : dir.listFiles()) {
                String name = file.getName();
                Matcher matcher = FILE_NAME.matcher(name);
                if(!matcher.find()) {
                    continue;
                }
                name = name.substring(9, name.indexOf(".properties"));
                String[] parts = name.split("_");
                String language = parts[0];
                String region = parts[1];
                LANGUAGES.put(language, ResourceBundle.getBundle("Language", new Locale(language, region), loader));
                COPYCAT.getLogger().log(SimpleLog.Level.INFO, "Registered the language \""+language+"\" with the region \""+region+"\".");
            }
            if(LANGUAGES.isEmpty()) {
                COPYCAT.getLogger().log(SimpleLog.Level.FATAL, "No valid languages were found, shutting down.");
                System.exit(1);
            }
        } catch(Exception exception) {
            COPYCAT.getLogger().log(SimpleLog.Level.FATAL, "An error occurred loading in the languages: "+exception.getMessage());
            throw new IllegalStateException("No valid languages have been found.");
        }
    }

    /**
     * Gets the message in the correct language
     * according to the guild specified.
     * @param guild The guild.
     * @param message Properties message path.
     * @return A valid message string.
     */
    public static String get(Guild guild, String message) {
        String locale;
        DataGuild localGuild = COPYCAT.getLocalGuilds().get(guild.getId());
        if(localGuild == null
            || localGuild.getLanguage() == null
            || !LANGUAGES.containsKey(localGuild.getLanguage().toLowerCase())) {
            locale = "en";
        } else {
            locale = localGuild.getLanguage();
        }
        try {
            return locale.equalsIgnoreCase("de") ?
                    LANGUAGES.get("de").getString(message) :
                    new String(LANGUAGES.get(locale).getString(message).getBytes("ISO-8859-1"), "UTF-8");
        } catch(Exception e) {
            try {
                return LANGUAGES.get("en").getString(message);
            } catch(Exception ex) {
                return "UNKNOWN_STRING";
            }
        }
    }

    /**
     * Gets all available languages.
     * @return A set of language keys.
     */
    public static Set<String> getLanguages() {
        return LANGUAGES.keySet();
    }

}
