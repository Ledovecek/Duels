package me.ledovec.duels.json;

import com.google.gson.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class ItemSerializer implements JsonSerializer<ItemStack> {

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject o = new JsonObject();
        if(src == null || src.getType().equals(Material.AIR)) {
            o.add("empty", p(true));
            return o;
        }
        o.add("type", p(src.getType().name()));
        o.add("amount", p(src.getAmount()));
        o.add("data", p((int) src.getDurability()));
        return o;
    }

    private JsonPrimitive p(String s) {
        return new JsonPrimitive(s);
    }

    private JsonPrimitive p(Boolean b) {
        return new JsonPrimitive(b);
    }

    private JsonPrimitive p(Integer i) {
        return new JsonPrimitive(i);
    }

}
