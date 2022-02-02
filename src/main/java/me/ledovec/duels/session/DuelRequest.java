package me.ledovec.duels.session;

import lombok.Getter;
import me.ledovec.duels.Constants;
import me.ledovec.duels.data.Kit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@Getter
public class DuelRequest extends RandomIdSession implements ExpirableSession {

    private final Player sender;
    private final Player receiver;

    @Nullable
    private Kit kit;

    public DuelRequest(Player sender, Player receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.kit = null;
    }

    @Override
    public long getExpireMillis() {
        return getBeginMillis() + Constants.DUEL_REQUEST_PERIOD;
    }

    public DuelRequest withKit(Kit kit) {
        this.kit = kit;
        return this;
    }

}
