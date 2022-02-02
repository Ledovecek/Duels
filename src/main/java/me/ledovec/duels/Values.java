package me.ledovec.duels;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Values {

    public static Values I = null;

    public final Location pos1 = new Location(Bukkit.getWorld("arena"), -3800, 50, -3816);
    public final Location pos2 = new Location(Bukkit.getWorld("arena"), -3800, 50, -3785);
    public final Location lobby = new Location(Bukkit.getWorld("world"), 0.5, 70, 0.5);

    public Values() {
        I = this;
    }

}
