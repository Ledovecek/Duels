package me.ledovec.duels.data;

import me.ledovec.duels.session.Session;
import org.jetbrains.annotations.NotNull;

public interface Listener<T extends Session> {

    boolean invoke(T object);
    @NotNull
    RepoAction getAction();
    @NotNull
    Class<T> getTargetType();

}
