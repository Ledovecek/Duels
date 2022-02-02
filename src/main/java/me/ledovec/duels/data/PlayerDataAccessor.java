package me.ledovec.duels.data;

import me.ledovec.duels.session.PlayerSession;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerDataAccessor implements SessionDataAccessor<PlayerSession> {

    @Override
    public @NotNull Class<PlayerSession> getTargetObjectType() {
        return PlayerSession.class;
    }

}
