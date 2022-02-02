package me.ledovec.duels.json;

import com.google.gson.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class ItemDeserializer implements JsonDeserializer<ItemStack> {

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json.isJsonObject()) {
            JsonObject o = json.getAsJsonObject();
            if(!o.has("empty") || !o.get("empty").getAsBoolean()) {
                String type = o.get("type").getAsString();
                int amount = o.get("amount").getAsInt();
                int data = o.get("data").getAsInt();
                Material mat = Material.matchMaterial(type);
                if(mat != null) {
                    return new ItemStack(mat, amount, (short) data);
                }
            }
        }
        return new ItemStack(Material.AIR);
    }

}
