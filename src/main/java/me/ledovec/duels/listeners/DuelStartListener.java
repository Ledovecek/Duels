package me.ledovec.duels.listeners;

import me.ledovec.duels.Duels;
import me.ledovec.duels.Values;
import me.ledovec.duels.data.Kit;
import me.ledovec.duels.data.Listener;
import me.ledovec.duels.data.RepoAction;
import me.ledovec.duels.session.DuelSession;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DuelStartListener implements Listener<DuelSession> {

    @Override
    public boolean invoke(DuelSession duelSession) {
        List<Player> players = duelSession.getPlayers();
        if(players.size() == 2 && players.stream().allMatch(Player::isOnline)) {
            Player p1 = players.get(0);
            Player p2 = players.get(1);
            return invokeStart(duelSession, p1, 0) && invokeStart(duelSession, p2, 1);
        }
        return false;
    }

    @Override
    public @NotNull RepoAction getAction() {
        return RepoAction.ADDON;
    }

    @Override
    public @NotNull Class<DuelSession> getTargetType() {
        return DuelSession.class;
    }

    private boolean invokeStart(DuelSession duelSession, Player p, int playerIndex) {
        Values vals = Values.I;
        p.teleport(
                playerIndex == 0 ? vals.pos1 : vals.pos2
        );
        Kit kit;
        if((kit = duelSession.getKit()) != null) {
            kit.getItems().forEach((index, item) -> {
                PlayerInventory inv = p.getInventory();
                if(index < inv.getSize()) {
                    inv.setItem(index, item);
                }
            });
        }
        p.sendTitle("§cDuels", "§7Good luck!", 10, 50, 30);
        p.sendMessage(Duels.PREFIX + "Game has started! Game ends in §c5 §7minutes.");
        p.setHealth(20.0);
        p.getActivePotionEffects().forEach(e -> p.removePotionEffect(e.getType()));
        return true;
    }

}
