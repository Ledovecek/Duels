package me.ledovec.duels.commands;

import me.ledovec.duels.Duels;
import me.ledovec.duels.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;

public class Stats_Command implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage(Duels.PREFIX + "§7Trying to find §a" + (args.length == 0 ? sender.getName() : args[0]) + " §7stats..");
            Util.runTaskAsync(() -> {
                long m1 = System.currentTimeMillis();
                ResultSet resultSet = Duels.INSTANCE.getMariaDB().execQuery("SELECT wins, loses, deaths, kills FROM Stats WHERE nick='" + (args.length == 0 ? sender.getName() : args[0]) + "'");
                try {
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            double kills = resultSet.getDouble("kills");
                            double deaths = resultSet.getDouble("deaths");
                            double kd = kills / deaths;
                            sender.sendMessage(Duels.PREFIX + "§7Stats found:");
                            sender.sendMessage("§7Player name: §a" + args[0]);
                            sender.sendMessage("§7Kills: §e" + kills);
                            sender.sendMessage("§7Deaths: §e" + deaths);
                            sender.sendMessage("§7K/D -> (" + kd + ")");
                            sender.sendMessage("§7Wins: §e" + resultSet.getInt("wins"));
                            sender.sendMessage("§7Loses: §e" + resultSet.getInt("loses"));
                        }
                        long msElapsedTime = System.currentTimeMillis() - m1;
                        sender.sendMessage("§8" + msElapsedTime + "ms");
                    } else {
                        sender.sendMessage(Duels.PREFIX + "§7Could not find stats for §c" + args[0]);
                    }
                    assert resultSet != null;
                    resultSet.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(Duels.PREFIX + "§7Could not find stats for §c" + args[0]);
                }
            });
        }
        sender.sendMessage(Duels.PREFIX +"You are not allowed to use stats command.");
        return false;
    }

}
