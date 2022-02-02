package me.ledovec.duels.commands;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import me.ledovec.duels.Duels;
import me.ledovec.duels.data.DuelsRepository;
import me.ledovec.duels.data.Kit;
import me.ledovec.duels.data.KitsRepository;
import me.ledovec.duels.session.DuelRequest;
import me.ledovec.duels.session.DuelSession;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class Duel_Command implements CommandExecutor {

    private final DuelsRepository repository;
    private final KitsRepository kitsRepository;

    public Duel_Command(DuelsRepository repository, KitsRepository kitsRepository) {
        this.repository = repository;
        this.kitsRepository = kitsRepository;
    }

    /*
    ARENA 1 ->
        pos1: -3800 50 -3816
        pos2: -3800 50 -3785
     */

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                sender.sendMessage(Duels.PREFIX + "Bad input. Use this commnd like this:");
                sender.sendMessage("    §e* §7/duel [name] <kit>");
            } else {
                if (args[0].equalsIgnoreCase("accept")) {
                    if (args.length > 1) {
                        Player from;
                        if ((from = Bukkit.getPlayer(args[1])) != null) {
                            List<DuelRequest> duelRequests = repository.getDuelRequests(sender.getName());
                            Optional<DuelRequest> duelRequestOptional = duelRequests.stream()
                                    .filter(req -> req.getSender().getName().equals(from.getName()))
                                    .findFirst();
                            if (duelRequestOptional.isPresent()) {
                                DuelRequest duelRequest = duelRequestOptional.get();
                                Player reqSender = duelRequest.getSender();
                                Player reqReceiver = duelRequest.getReceiver();
                                repository.remove(DuelRequest.class, s -> s.equals(duelRequest));
                                DuelSession duelSession = repository.save(() -> new DuelSession(reqSender, reqReceiver).withKit(duelRequest.getKit()), DuelSession.class);
                                reqSender.sendMessage(
                                        duelSession != null
                                        ? Duels.PREFIX + "§a" + reqReceiver.getName() + " §7accepted your duel request." : Duels.PREFIX + "§7We're sorry, something had gone wrong."
                                );
                                sender.sendMessage(
                                        duelSession != null
                                                ? Duels.PREFIX + "§7Duel request has been accepted." : Duels.PREFIX + "§7We're sorry, something had gone wrong."
                                );
                            } else {
                                sender.sendMessage(Duels.PREFIX + "You have no request from this player.");
                            }
                        } else {
                            sender.sendMessage(Duels.PREFIX + "This player is not online.");
                        }
                    } else {
                        sender.sendMessage(Duels.PREFIX + "You must specify the player.");
                        sender.sendMessage(Duels.PREFIX + "Usage: §f/duel accept [player]");
                    }
                } else if (args[0].equalsIgnoreCase("decline")) {
                    if (args.length > 1) {
                        Player from;
                        if ((from = Bukkit.getPlayer(args[1])) != null) {
                            List<DuelRequest> duelRequests = repository.getDuelRequests(sender.getName());
                            Optional<DuelRequest> duelRequestOptional = duelRequests.stream()
                                    .filter(req -> req.getSender().getName().equals(from.getName()))
                                    .findFirst();
                            if (duelRequestOptional.isPresent()) {
                                DuelRequest duelRequest = duelRequestOptional.get();
                                Player reqSender = duelRequest.getSender();
                                Player reqReceiver = duelRequest.getReceiver();
                                repository.remove(DuelRequest.class, s -> s.equals(duelRequest));
                                reqSender.sendMessage(Duels.PREFIX + "§a" + reqReceiver.getName() + " §7declined your duel request.");
                                sender.sendMessage(Duels.PREFIX + "§7Duel request has been declined.");
                            } else {
                                sender.sendMessage(Duels.PREFIX + "You have no request from this player.");
                            }
                        } else {
                            sender.sendMessage(Duels.PREFIX + "This player is not online.");
                        }
                    } else {
                        sender.sendMessage(Duels.PREFIX + "You must specify the player.");
                        sender.sendMessage(Duels.PREFIX + "Usage: §f/duel decline [player]");
                    }
                } else {
                    Player receiver;
                    if((receiver = Bukkit.getPlayer(args[0])) == null) {
                        sender.sendMessage(Duels.PREFIX + "§7Player §c" + args[0] + " §7could not be found.");
                        return true;
                    }
                    if(receiver.getName().equals(sender.getName())) {
                        sender.sendMessage(Duels.PREFIX + "Hey! You can not invite yourself :/");
                        sender.sendMessage(Duels.PREFIX + "§7Find some friends!");
                        return true;
                    }
                    if(args.length <= 1) {
                        sender.sendMessage(Duels.PREFIX + "You must select kit for this game:");
                        sender.sendMessage(Duels.PREFIX + "Available kits: §f" + Joiner.on(", ").join(kitsRepository.getKits().keySet()));
                        return true;
                    }
                    @Nullable Kit kit = kitsRepository.getKit(args[1]);
                    DuelRequest req = repository.save(() -> new DuelRequest(((Player) sender).getPlayer(), receiver), DuelRequest.class);
                    if(req != null && kit != null) {
                        req.withKit(kit);
                    }
                    receiver.sendMessage(Duels.PREFIX + "§7Player §a" + sender.getName() + " §7sent you duel request.");
                    BaseComponent[] msg = new ComponentBuilder("         ")
                            .append("§a[ACCEPT]").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + sender.getName()))
                            .append("      ")
                            .append("§c[DECLINE]").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel decline " + sender.getName()))
                            .create();
                    receiver.spigot().sendMessage(msg);
                    sender.sendMessage(Duels.PREFIX + "§7Duels request sent to player §a" + args[0]);
                }
            }
        } else {
            System.out.println("[DUELS] You can not use this command through console, sorry.");
        }
        return false;
    }

}
