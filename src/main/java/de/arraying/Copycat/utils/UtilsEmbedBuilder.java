package de.arraying.Copycat.utils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
public class UtilsEmbedBuilder extends EmbedBuilder {

    private Field fieldsField;
    private List<MessageEmbed.Field> fields;

    /**
     * The constructor, which will set things up, and send a stacktrace is an error occurs.
     */
    public UtilsEmbedBuilder() {
        try {
            fieldsField = EmbedBuilder.class.getDeclaredField("fields");
            fieldsField.setAccessible(true);
            pullUpdate();
        } catch(NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a field from the EmbedBuilder.
     * @param title The name of the field you wish to remove.
     * @return The UtilsEmbedBuilder instance.
     */
    public UtilsEmbedBuilder removeField(String title) {
        pullUpdate();
        List<MessageEmbed.Field> toRemvove = new ArrayList<>();
        fields.stream().filter(fStream -> fStream.getName().equalsIgnoreCase(title))
                .forEach(fStream -> toRemvove.add(fStream));
        fields.removeAll(toRemvove);
        pushUpdate();
        return this;
    }

    /**
     * Changes the value of a field from the EmbedBuilder.
     * @param title The name of the field you wish to change the value of.
     * @param value The new value of the field.
     * @return The UtilsEmbedBuilder instance.
     */
    public UtilsEmbedBuilder setFieldValue(String title, String value) {
        if(value.length() > 1024) {
            value = value.substring(value.length()-6);
            value += "[...]";
        }
        final String finalValue = value;
        pullUpdate();
        fields.stream().filter(fStream -> fStream.getName().equalsIgnoreCase(title))
                .forEach(fStream -> {
                    try {
                        Field fStreamField = fStream.getClass().getDeclaredField("value");
                        fStreamField.setAccessible(true);
                        fStreamField.set(fStream, finalValue);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
        return this;
    }

    /**
     * Replaces a string in the value of a field from the EmbedBuilder.
     * @param title The name of the field you wish to malipulate.
     * @param from The string it will replace.
     * @param to The string it will replace to.
     * @return The UtilsEmbedBuilder instance.
     */
    public UtilsEmbedBuilder replaceFieldValue(String title, String from, String to) {
        pullUpdate();
        fields.stream().filter(fStream -> fStream.getName().equalsIgnoreCase(title))
                .forEach(fStream -> {
                    try {
                        Field fStreamField = fStream.getClass().getDeclaredField("value");
                        fStreamField.setAccessible(true);
                        String value = (String) fStreamField.get(fStream);
                        fStreamField.set(fStream, value.replace(from, to));
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
        return this;
    }

    /**
     * Changes whether a field should be displayed inline from the EmbedBuilder.
     * @param title The name fo the field you wish to change the inline property of.
     * @param inline The new inline value.
     * @return The UtilsEmbedBuilder instance
     */
    public UtilsEmbedBuilder setFieldInline(String title, boolean inline) {
        pullUpdate();
        fields.stream().filter(fStream -> fStream.getName().equalsIgnoreCase(title))
                .forEach(fStream -> {
                    try {
                        Field fStreamField = fStream.getClass().getDeclaredField("inline");
                        fStreamField.setAccessible(true);
                        fStreamField.set(fStream, inline);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
        return this;
    }

    /**
     * This method updates the local MessageEmbed.Field list.
     */
    private void pullUpdate() {
        try {
            fields = (List<MessageEmbed.Field>) fieldsField.get(this);
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method updates the EmbedBuilder MessageEmbed.Field list.
     */
    private void pushUpdate() {
        try {
            fieldsField.set(this, fields);
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}

