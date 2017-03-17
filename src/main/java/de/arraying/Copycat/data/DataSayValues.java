package de.arraying.Copycat.data;

import lombok.Data;

import java.util.ArrayList;

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
public @Data class DataSayValues {

    private final ArrayList<String> userReceivers;
    private final ArrayList<String> channelReceivers;
    private boolean silent;
    private boolean delete;
    private int delay;

    /**
     * Creates a new instance of data for the say command.
     */
    public DataSayValues() {
        this.userReceivers = new ArrayList<>();
        this.channelReceivers = new ArrayList<>();
        this.silent = false;
        this.delete = false;
        this.delay = -1;
    }

}
