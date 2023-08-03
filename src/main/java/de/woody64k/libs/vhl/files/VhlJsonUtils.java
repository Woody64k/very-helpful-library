package de.woody64k.libs.vhl.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class VhlJsonUtils {
    private static Gson gson;

    public static <T> T readJsonFromClasspath(String fileName, Class<T> t) {
        return readJsonFromUrl(VhlFileUtils.findUrlOnClasspath(fileName), t);
    }

    private static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder().create();
        }
        return gson;
    }

    public static <T> T readJson(String content, Class<T> t) {
        return getGson().fromJson(content, t);
    }

    private static <T> T readJsonFromUrl(URL url, Class<T> t) {
        String content = VhlFileUtils.readContentsFromUrl(url);
        return getGson().fromJson(content, t);
    }

    public static <T> T readJsonFromFile(File file, Class<T> t) {
        try (JsonReader reader = new JsonReader(new FileReader(file))) {
            return getGson().fromJson(reader, t);
        } catch (IOException e) {
            try {
                return t.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
                throw new RuntimeException(e);
            }
        }
    }

    public static StringBuilder replaceInString(StringBuilder template, String placeholder, StringBuilder content) {
        int start = template.indexOf(placeholder);
        if (start >= 0) {
            int end = start + placeholder.length();
            template.replace(start, end, content.toString());
        }
        return template;
    }

    public static String readFile(File path) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path.getAbsolutePath()));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void writeGsonToFile(File file, T object) {
        try {
            VhlFileUtils.makeFileExists(file);
            try (Writer writer = new FileWriter(file); Writer fileWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                getGson().toJson(object, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void replaceInStringBuilder(StringBuilder template, Matcher m, String replacemant) {
        int start = 0;
        while (m.find(start)) {
            template.replace(m.start(), m.end(), replacemant);
            start = m.start() + replacemant.length();
        }
    }
}
