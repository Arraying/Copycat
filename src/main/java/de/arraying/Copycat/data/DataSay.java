package de.arraying.Copycat.data;

import java.util.HashMap;

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
public class DataSay {

    private static final HashMap<String, DataSayValues> data = new HashMap<>();

    /**
     * Adds a new entry to the map.
     * @param id The snowflake ID of the entry.
     */
    public static void start(String id) {
        data.put(id, new DataSayValues());
    }
    /**
     * Retrieves the data corresponding to the ID.
     * @param id The ID of the entry.
     * @return A DataSayValues instance or null if the ID does not exist.
     */
    public static DataSayValues retrieve(String id) {
        return data.get(id);
    }

    /**
     * Removes the entry as it is no longer needed.
     * @param id The ID of the entry.
     */
    public static void complete(String id) {
        data.remove(id);
    }

}
