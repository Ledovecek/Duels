package me.ledovec.duels.listeners;

import me.ledovec.duels.Duels;
import me.ledovec.duels.Values;
import me.ledovec.duels.data.Listener;
import me.ledovec.duels.data.RepoAction;
import me.ledovec.duels.database.MariaDB;
import me.ledovec.duels.session.DuelSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;

import static java.util.concurrent.CompletableFuture.runAsync;

public class DuelEndListener implements Listener<DuelSession> {

    private final MariaDB mariaDB;

    public DuelEndListener(MariaDB mariaDB) {
        this.mariaDB = mariaDB;
    }

    @Override
    public boolean invoke(DuelSession duelSession) {
        duelSession.getPlayers().forEach(p -> invokeEnd(duelSession, p));
        return true;
    }

    @Override
    public @NotNull RepoAction getAction() {
        return RepoAction.REMOVAL;
    }

    @Override
    public @NotNull Class<DuelSession> getTargetType() {
        return DuelSession.class;
    }

    private void invokeEnd(DuelSession duelSession, Player p) {
        Values vals = Values.I;
        String winner = duelSession.getWinner();
        boolean isWinner = p.getName().equals(winner);
        p.getInventory().clear();
        p.setHealth(p.getMaxHealth());
        p.sendMessage(
                isWinner
                ? Duels.PREFIX + "Congratulation! You won the duel!"
                        : Duels.PREFIX + "Duel ended. You lost it :("
        );
        p.sendTitle(isWinner ? "§6§lWinner!" : "§c§l" + winner + " §7won", isWinner ? "§7You won the duel!" : "§7You lost the duel", 20, 70, 30);
        Bukkit.getScheduler().runTaskLater(Duels.INSTANCE, () -> {
            p.teleport(vals.lobby);
        }, 20*3L);
        runAsync(() -> {
            try {
                if(isWinner) {
                    ResultSet resultSet = mariaDB.execQuery("SELECT kills FROM Stats WHERE nick='" + p.getName() + "';");
                    if(resultSet.next()) {
                        int anInt = resultSet.getInt(1);
                        mariaDB.exec("UPDATE Stats SET wins = " + (anInt + 1) + ", kills = " + (anInt + 1) +  " WHERE nick='" + p.getName() + "';");
                    }
                } else {
                    ResultSet killerResultSet = mariaDB.execQuery("SELECT deaths FROM Stats WHERE nick='" + p.getName() + "'");
                    if (killerResultSet.next()) {
                        mariaDB.exec("UPDATE Stats SET loses = " +
                                (killerResultSet.getInt(1) + 1)
                                + ", deaths = " + (killerResultSet.getInt(1) + 1)
                                + " WHERE nick='" + p.getName() + "';");
                    }
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
    }

}
