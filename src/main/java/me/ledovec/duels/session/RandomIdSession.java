package me.ledovec.duels.session;

import lombok.Getter;
import org.apache.commons.lang.RandomStringUtils;

@Getter
public abstract class RandomIdSession implements Session {

    private final String sessionCode;
    private final long beginMillis;

    public RandomIdSession() {
        this.sessionCode = RandomStringUtils.randomAlphanumeric(8);
        this.beginMillis = System.currentTimeMillis();
    }

}
