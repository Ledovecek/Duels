package me.ledovec.duels.data;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import lombok.Getter;
import me.ledovec.duels.Constants;
import me.ledovec.duels.Duels;
import me.ledovec.duels.json.ItemDeserializer;
import me.ledovec.duels.json.ItemSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class KitsRepository {

    private static final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(Kit.class, new ItemSerializer())
            .registerTypeAdapter(Kit.class, new ItemDeserializer())
            .create();

    @Getter
    private final Map<String, Kit> kits = Maps.newConcurrentMap();
    private final Duels plugin;

    public KitsRepository(Duels plugin) {
        this.plugin = plugin;
        reload();
        init();
    }

    public void reload() {
        kits.clear();
        File kitsFile = new File(plugin.getDataFolder().getAbsolutePath() + Constants.KITS_PATH);
        Type type = new TypeToken<Map<String, Map<Integer, ItemStack>>>(){}.getType();
        try {
            boolean existed = true;
            if(!kitsFile.exists()) {
                existed = false;
                kitsFile.mkdirs();
                kitsFile.delete();
                kitsFile.createNewFile();
            }
            kitsFile = new File(kitsFile.getAbsolutePath());
            if(!existed) {
                FileWriter writer = new FileWriter(kitsFile);
                writer.write("{}");
                writer.close();
            }
            FileReader fileReader = new FileReader(kitsFile);
            JsonReader reader = new JsonReader(fileReader);
            Map<String, Map<Integer, ItemStack>> map = GSON.fromJson(reader, type);
            map.forEach((id, items) -> registerKit(id, new Kit(items)));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void registerKit(String idIgnoringCase, Kit kit) {
        idIgnoringCase = idIgnoringCase.toUpperCase();
        this.kits.put(idIgnoringCase, kit);
    }

    public Optional<Kit> getRandomKit() {
        Collection<Kit> vals = new ArrayList<>(kits.values());
        if(vals.isEmpty()) return Optional.empty();
        Kit[] kitsArray = vals.toArray(new Kit[0]);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return Optional.ofNullable(kitsArray[random.nextInt(kitsArray.length)]);
    }

    @Nullable
    public Kit getKit(String idIgnoringCase) {
        return kits.get(idIgnoringCase.toUpperCase());
    }

    private void init() {
        Map<Integer, ItemStack> items = Maps.newConcurrentMap();
        items.put(0, new ItemStack(Material.IRON_SWORD, 1));
        Kit kit = new Kit(items);
        registerKit("DEFAULT", kit);
    }

}
