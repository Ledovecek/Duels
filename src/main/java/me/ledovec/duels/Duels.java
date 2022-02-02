package me.ledovec.duels;

import com.google.gson.Gson;
import lombok.Getter;
import me.ledovec.duels.commands.Duel_Command;
import me.ledovec.duels.commands.Kits_Command;
import me.ledovec.duels.commands.Stats_Command;
import me.ledovec.duels.data.DuelsRepository;
import me.ledovec.duels.data.KitsRepository;
import me.ledovec.duels.database.DatabaseController;
import me.ledovec.duels.database.MariaDB;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class Duels extends JavaPlugin {

    public static final String PREFIX = "§7[§cDuels§7] §7";
    public static final Gson GSON = new Gson();
    public static Duels INSTANCE = null;

    @Getter
    private final MariaDB mariaDB = new MariaDB();

    @Getter
    private DuelsRepository duelsRepository;
    @Getter
    private KitsRepository kitsRepository;
    @Getter
    private DatabaseController databaseController;

    @SuppressWarnings("all")
    @Override
    public void onEnable() {
        INSTANCE = this;
        new Values();
        kitsRepository = new KitsRepository(this);
        duelsRepository = new DuelsRepository(this);
        databaseController = new DatabaseController(this);
        if(!duelsRepository.reload()) {
            getLog4JLogger().error("Error while loading duels repository, plugin is disabling...");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginCommand("duel").setExecutor(new Duel_Command(duelsRepository, kitsRepository));
        Bukkit.getPluginCommand("kits").setExecutor(new Kits_Command());
        Bukkit.getPluginCommand("stats").setExecutor(new Stats_Command());
        Bukkit.getPluginManager().registerEvents(new DuelsListener(this), this);
        mariaDB.connect("<user>", "<pwd>", "jdbc:mariadb://<database>:3306/Duels", "org.mariadb.jdbc.Driver");
        System.out.println(PREFIX + "Plugin loaded successfully");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll();
    }

}
