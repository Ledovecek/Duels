package me.ledovec.duels.session;

import lombok.Getter;
import me.ledovec.duels.Duels;
import me.ledovec.duels.data.DuelsRepository;
import org.jetbrains.annotations.Nullable;

public class PlayerSession extends RandomIdSession {

    @Getter
    private final String nick;

    protected PlayerSession(String nick) {
        this.nick = nick;
    }

    public DuelSession getDuelSession() {
        return getDuelSession(Duels.INSTANCE.getDuelsRepository());
    }

    @Nullable
    public DuelSession getDuelSession(DuelsRepository repository) {
        return repository.getDuelSession(nick).orElse(null);
    }

}
