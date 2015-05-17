package com.lenis0012.chatango.bot.utils;

import com.google.common.collect.Sets;
import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Config wrapper to simplify json reading, setting and updating.
 */
public class JsonConfig {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().setVersion(1.0).create();
    private final JsonParser parser = new JsonParser();
    private final File file;
    private JsonObject object;
    private Method setValue;

    /**
     * Create a new configuration wrapper for JSON.
     *
     * @param file Local file on disk
     */
    public JsonConfig(String file) {
        this.file = new File(file);
        reload();
    }

    /**
     * Reload configuration from file on disk.
     */
    public void reload() {
        if(file.exists()) {
            try {
                object = (JsonObject) parser.parse(new FileReader(file));
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else {
            object = new JsonObject();
        }
    }

    /**
     * Set a value is json object
     *
     * @param key   Path and key (destination) of the value
     * @param value The value
     */
    public void set(String key, Object value) {
        // Create a new instance
        JsonElement primitive = value instanceof JsonElement ? (JsonElement) value : new JsonPrimitive(true);

        // Now magically inject the actual value
        if(!(value instanceof JsonElement)) {
            try {
                if(setValue == null) {
                    setValue = JsonPrimitive.class.getDeclaredMethod("setValue", Object.class);
                    setValue.setAccessible(true);
                }
                setValue.invoke(primitive, value);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        // Now find/create the proper object
        JsonObject current = getObject(key, true, true);
        String[] keys = key.split("\\.");

        // Set the value
        current.add(keys[keys.length - 1], primitive);
    }

    /**
     * Save json object to file on disk.
     */
    public void save() {
        String jsonString = gson.toJson(object);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(jsonString);
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public JsonPrimitive get(String key, JsonPrimitive def) {
        if(has(key)) {
            return get(key);
        } else {
            set(key, def);
            return def;
        }
    }

    /**
     * Get JSON element from the object.
     *
     * @param key Path and key of the value
     * @return Json primitive value
     */
    public JsonPrimitive get(String key) {
        JsonObject current = getObject(key, true);
        String[] keys = key.split("\\.");
        return current.getAsJsonPrimitive(keys[keys.length - 1]);
    }

    public JsonArray getList(String key, JsonArray def) {
        if(has(key)) {
            return getList(key);
        } else {
            set(key, def);
            return def;
        }
    }

    public JsonArray getList(String key) {
        JsonObject current = getObject(key, true);
        String[] keys = key.split("\\.");
        return current.getAsJsonArray(keys[keys.length - 1]);
    }

    /**
     * Check whether or not the json object has a certain key.
     *
     * @param path The path and key
     * @return True if the key is found, false otherwise
     */
    public boolean has(String path) {
        String[] keys = path.split("\\.");
        JsonObject current = getObject(path, true);
        return current.has(keys[keys.length - 1]);
    }

    /**
     * Return a list of all keys in a path
     *
     * @param path The path to use
     * @return A list of all keys
     */
    public Set<String> keys(String path) {
        JsonObject current = getObject(path, false);
        Set<String> set = Sets.newHashSet();
        for(Entry<String, JsonElement> entry : current.entrySet()) {
            set.add(entry.getKey());
        }

        return set;
    }

    /**
     * Remove a key from the json object, including all of its sub objects.
     *
     * @param path Path to remove from json object
     */
    public void remove(String path) {
        JsonObject current = getObject(path, true);
        String[] keys = path.split("\\.");
        current.remove(keys[keys.length - 1]);
    }

    private JsonObject getObject(String path, boolean pathOnly) {
        return getObject(path, pathOnly, false);
    }

    private JsonObject getObject(String path, boolean pathOnly, boolean write) {
        JsonObject current = object;
        String[] keys = path.split("\\.");
        int alt = pathOnly ? 1 : 0;
        if(keys.length - alt <= 0) {
            // Return root node
            return current;
        }

        for(int i = 0; i < keys.length - alt; i++) {
            if(current.has(keys[i])) {
                current = current.getAsJsonObject(keys[i]);
            } else if(!write) {
                current = new JsonObject();
                break;
            } else {
                JsonObject next = new JsonObject();
                current.add(keys[i], next);
                current = next;
            }
        }

        return current;
    }
}
