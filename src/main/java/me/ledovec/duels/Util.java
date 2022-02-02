package me.ledovec.duels;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Util {

    public static void runTask(Runnable task) {
        Bukkit.getScheduler().runTask(Duels.INSTANCE, task);
    }

    public static void runTaskAsync(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(Duels.INSTANCE, task);
    }

    public static void callEvent(Event event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public static <T> List<T> synchronizedList() {
        return synchronizedList(Lists.newArrayList());
    }

    @SafeVarargs
    public static <T> List<T> synchronizedList(T... objects) {
        return synchronizedList(Lists.newArrayList(objects));
    }

    public static <T> List<T> synchronizedList(List<T> objects) {
        return Collections.synchronizedList(objects);
    }

}
