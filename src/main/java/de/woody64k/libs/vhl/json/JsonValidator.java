package de.woody64k.libs.vhl.json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

public class JsonValidator {

    /**
     * Checks if all fields in JSON are mapped to the class. Returns the List of the
     * Json-Path of the not Parsed Properties.
     * 
     * @param json
     * @param type
     * @return
     */
    public static List<String> validate(File json, Class<?> type) {
        try (JsonReader reader = new JsonReader(new FileReader(json))) {
            Gson GSON = new GsonBuilder().create();
            JsonObject jsonObject = GSON.fromJson(reader, JsonObject.class);

            return validateJson(jsonObject, type, "root");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> validateJson(JsonObject jsonObject, Class<?> type, String context) {
        List<String> results = new ArrayList<>();
        Map<String, Field> classFields = Arrays.stream(type.getDeclaredFields()).collect(Collectors.toMap(Field::getName, Function.identity()));
        for (Entry<String, JsonElement> jsonEntry : jsonObject.entrySet()) {
            Field fieldInClass = getFieldFromClass(classFields, jsonEntry);
            String newContext = String.format("%s.%s", context, jsonEntry.getKey());
            if (fieldInClass == null) {
                results.add(newContext);
            } else {
                if (jsonEntry.getValue().isJsonObject()) {
                    Class<?> nextType = fieldInClass.getType();
                    results.addAll(validateJson(jsonEntry.getValue().getAsJsonObject(), nextType, newContext));
                } else if (jsonEntry.getValue().isJsonArray()) {
                    Class<?> nextType = (Class<?>) ((ParameterizedType) fieldInClass.getGenericType()).getActualTypeArguments()[0];
                    int i = 0;
                    for (JsonElement jsonListEntry : jsonEntry.getValue().getAsJsonArray()) {
                        results.addAll(validateJson(jsonListEntry.getAsJsonObject(), nextType, String.format("%s[%d]", newContext, i++)));
                    }
                }
            }
        }
        return results;
    }

    /**
     * Check if a field exists in class or a field is annotaded accordingly.
     * 
     * @param classFields
     * @param jsonEntry
     * @return
     */
    private static Field getFieldFromClass(Map<String, Field> classFields, Entry<String, JsonElement> jsonEntry) {
        Field fieldInClass = classFields.get(jsonEntry.getKey());
        if (fieldInClass == null) {
            for (Field field : classFields.values()) {
                SerializedName[] anotation = field.getAnnotationsByType(SerializedName.class);
                if (anotation != null && anotation.length > 0 && jsonEntry.getKey().equalsIgnoreCase(anotation[0].value())) {
                    return field;
                }
            }
        }
        return fieldInClass;
    }
}
