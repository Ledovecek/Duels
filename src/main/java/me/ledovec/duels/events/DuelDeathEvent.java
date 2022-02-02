package me.ledovec.duels.events;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class DuelDeathEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    private final Player target;
    private final Entity damager;

    public DuelDeathEvent(@NotNull Player target, @Nullable Entity damager) {
        this.target = target;
        this.damager = damager;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public boolean isPlayerDamager() {
        return damager instanceof Player;
    }

}
