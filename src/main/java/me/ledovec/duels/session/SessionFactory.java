package me.ledovec.duels.session;

@FunctionalInterface
public interface SessionFactory<T extends Session> {

    T create();

}
