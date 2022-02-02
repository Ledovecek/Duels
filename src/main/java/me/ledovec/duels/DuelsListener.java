package me.ledovec.duels;

import lombok.SneakyThrows;
import me.ledovec.duels.data.DuelsRepository;
import me.ledovec.duels.database.DatabaseController;
import me.ledovec.duels.database.MariaDB;
import me.ledovec.duels.events.DuelDeathEvent;
import me.ledovec.duels.session.DuelRequest;
import me.ledovec.duels.session.DuelSession;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static me.ledovec.duels.Util.callEvent;

public class DuelsListener implements Listener {

    private final MariaDB mariaDB;
    private final DuelsRepository duelsRepository;
    private final Duels plugin;
    private DatabaseController databaseController;

    public DuelsListener(Duels plugin) {
        this.mariaDB = plugin.getMariaDB();
        this.duelsRepository = plugin.getDuelsRepository();
        this.plugin = plugin;
        this.databaseController = plugin.getDatabaseController();
    }

    @SneakyThrows
    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        String nick = e.getName();
        databaseController.create(nick);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.teleport(new Location(Bukkit.getWorld("world"), 0.5, 70, 0.5));
        e.setJoinMessage(Duels.PREFIX + "§7Hey! §a" + player.getName() + " §7has joined the server!");
        player.sendTitle("§6Welcome!", "§7Have fun and enjoy playing!", 20, 70, 30);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        e.setQuitMessage(Duels.PREFIX + "§7Oh no.. §c" + player.getName() + " §7left the server.");
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player) {
            Player entity = (Player) e.getEntity();
            if(e.getDamager() instanceof Player && !isInDuel((Player) e.getDamager())) {
                e.setCancelled(true);
                return;
            }
            if(isInDuel(entity) && (entity.getHealth() - e.getFinalDamage()) <= 0.0) {
                e.setCancelled(true);
                DuelSession duel = getDuel(entity);
                setWinner(duel, entity);
                duelsRepository.remove(DuelSession.class, duelSession -> duelSession.equals(duel));
                callEvent(new DuelDeathEvent(entity, e.getDamager()));
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            Player entity = (Player) e.getEntity();
            if(isInDuel(entity) && (entity.getHealth() - e.getFinalDamage()) <= 0.0) {
                e.setCancelled(true);
                DuelSession duel = getDuel(entity);
                setWinner(duel, entity);
                duelsRepository.remove(DuelSession.class, duelSession -> duelSession.equals(duel));
                callEvent(new DuelDeathEvent(entity, null));
            }
        }
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage();
        Player p = e.getPlayer();
        if(message.equalsIgnoreCase("/accept") || (message.contains(" ") && message.split(" ")[0].equalsIgnoreCase("/accept"))) {
            p.performCommand(message.replaceFirst("/accept", "duel"));
        }
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        Player p = e.getPlayer();
        Entity entity = e.getRightClicked();
        if(entity instanceof Player && !isInDuel((Player) entity)) {
            List<DuelRequest> duelRequests = duelsRepository.getDuelRequests(p.getName());
            Optional<DuelRequest> duelRequest = duelRequests.stream()
                    .filter(d -> d.getSender().getName().equals(entity.getName()))
                    .findFirst();
            duelRequest.ifPresent(req -> {
                Player sender = req.getSender();
                p.performCommand("duel accept " + sender.getName());
            });
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (isInDuel(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockbreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (isInDuel(p)) {
            e.setCancelled(true);
        }
    }

    private void setWinner(DuelSession duelSession, Player playerExcluded) {
        setWinner(duelSession, playerExcluded.getName());
    }

    private void setWinner(DuelSession duelSession, String nickExcluded) {
        duelSession.getPlayers().stream()
                .filter(p -> !p.getName().equals(nickExcluded))
                .forEach(p -> duelSession.setWinner(p.getName()));
    }

    public boolean isInDuel(Player p) {
        return isInDuel(p.getName());
    }

    public boolean isInDuel(String nick) {
        return getDuel(nick) != null;
    }

    @Nullable
    private DuelSession getDuel(Player p) {
        return getDuel(p.getName());
    }

    @Nullable
    private DuelSession getDuel(String nick) {
        return duelsRepository.getDuelSession(nick).orElse(null);
    }

}
