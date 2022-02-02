package me.ledovec.duels.data;

import me.ledovec.duels.session.DuelSession;
import org.jetbrains.annotations.NotNull;

public abstract class DuelDataAccessor implements SessionDataAccessor<DuelSession> {

    @Override
    public @NotNull Class<DuelSession> getTargetObjectType() {
        return DuelSession.class;
    }

}
