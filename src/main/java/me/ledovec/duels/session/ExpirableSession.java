package me.ledovec.duels.session;

public interface ExpirableSession extends Session {

    default boolean hasExpired() {
        return System.currentTimeMillis() >= getExpireMillis();
    }

    long getExpireMillis();

}
