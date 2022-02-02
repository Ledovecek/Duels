package me.ledovec.duels.commands;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import me.ledovec.duels.Constants;
import me.ledovec.duels.Duels;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static me.ledovec.duels.Duels.GSON;

public class Kits_Command implements CommandExecutor {

    @SneakyThrows
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                sender.sendMessage(Duels.PREFIX + "§cThis is debug command only!");
                sender.sendMessage(Duels.PREFIX + "§7Available kits: §f" + Arrays.toString(new File(Constants.KITS_PATH).listFiles()));
            } else {
                if (args[0].equalsIgnoreCase("create")) {
                    if (!args[1].isEmpty()) {
                        File file = new File(Constants.KITS_PATH + args[1] + ".json");
                        PrintWriter out = new PrintWriter(file);
                        if (!file.exists()) {
                            sender.sendMessage(Duels.PREFIX + "This kit already exists.");
                        } else {
                            boolean newFile = file.createNewFile();
                            if (newFile) {
                                sender.sendMessage(Duels.PREFIX + "Kit has been created.");
                            } else {
                                sender.sendMessage(Duels.PREFIX + "Kit creating failed!");
                            }
                        }
                    } else {
                        sender.sendMessage(Duels.PREFIX + "Select kit name!");
                    }
                }
            }
        } else {
            System.out.println(Duels.PREFIX + "You can not use this command.");
        }
        return false;
    }

}
