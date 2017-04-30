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
public @Data class DataGuild {

    private final String id;
    private final String prefix;
    private final String language;
    private final String footer;

    /**
     * Creates a new guild data object.
     * @param id The ID of the guild.
     * @param prefix The prefix of the guild.
     * @param language The language of the guild.
     * @param footer The PM footer of the guild.
     */
    public DataGuild(String id, String prefix, String language, String footer) {
        this.id = id;
        this.prefix = prefix;
        this.language = language;
        this.footer = footer;
    }

}
