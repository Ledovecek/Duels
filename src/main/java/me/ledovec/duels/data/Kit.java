package me.ledovec.duels.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@AllArgsConstructor
@Data
public class Kit {

    private final Map<Integer, ItemStack> items;

}
