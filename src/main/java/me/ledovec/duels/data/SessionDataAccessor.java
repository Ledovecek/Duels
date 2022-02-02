package me.ledovec.duels.data;

import me.ledovec.duels.session.Session;
import org.jetbrains.annotations.NotNull;

public interface SessionDataAccessor<T extends Session> {

    boolean modify(T object);
    @NotNull
    Class<T> getTargetObjectType();

}
