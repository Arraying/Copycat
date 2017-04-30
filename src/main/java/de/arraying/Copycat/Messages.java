package de.arraying.Copycat;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

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

    private static HashMap<String, ResourceBundle> languages;

    /**
     * Initialises the messages.
     */
    public static void init() {
        try {
            File dir = new File("locales");
            if(!dir.exists()) {
                Copycat.getInstance().getLogger().log(SimpleLog.Level.FATAL, "The languages are not present, shutting down.");
                System.exit(1);
            }
            languages = new HashMap<>();
            String prefix = "Language_";
            String suffix = ".properties";
            URL[] urls = {dir.toURI().toURL()};
            ClassLoader loader = new URLClassLoader(urls);
            for(File file : dir.listFiles()) {
                if(file.getName().startsWith(prefix)
                        && file.getName().endsWith(suffix)) {
                    String name = file.getName().substring(prefix.length(), file.getName().indexOf(suffix));
                    String[] parts = name.split("_");
                    Locale locale = new Locale(parts[0], parts[1]);
                    ResourceBundle resourceBundle = ResourceBundle.getBundle("Language", locale, loader);
                    languages.put(parts[0], resourceBundle);
                    Copycat.getInstance().getLogger().log(SimpleLog.Level.INFO, "Registered the language \""+parts[0]+"_"+parts[1]+"\".");
                }
            }
            if(languages.isEmpty()) {
                Copycat.getInstance().getLogger().log(SimpleLog.Level.FATAL, "No valid languages were found, shutting down.");
                System.exit(1);
            }
        } catch(Exception e) {
            Copycat.getInstance().getLogger().log(SimpleLog.Level.FATAL, "An error occurred loading in the languages: "+e.getMessage());
        }
    }

    /**
     * Gets the message for a spesific language.
     * @param guild The guild.
     * @param message Properties message path.
     * @return A valid message string.
     */
    public static String get(Guild guild, String message) {
        String locale = Copycat.getInstance().getLocalGuilds().get(guild.getId()).getLanguage();
        if(locale == null
            || !languages.containsKey(locale.toLowerCase())) {
            locale = "en";
            Copycat.getInstance().getDataManager().setGuildValue("language", guild.getId(), "en");
        }
        try {
            return languages.get(locale).getString(message);
        } catch(MissingResourceException e) {
            try {
                return languages.get("en").getString(message);
            } catch(MissingResourceException ex) {
                return "UNKNOWN_STRING";
            }
        }
    }

    /**
     * Gets all available languages.
     * @return A set of language keys.
     */
    public static Set<String> getLanguages() {
        return languages.keySet();
    }

}
