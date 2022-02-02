package me.ledovec.duels.session;

import com.google.common.collect.Maps;
import lombok.Getter;
import me.ledovec.duels.Constants;
import me.ledovec.duels.data.Kit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static me.ledovec.duels.Util.synchronizedList;

@Getter
public class DuelSession extends RandomIdSession implements ExpirableSession {

    private final List<Player> players;
    private final Map<String, String> data;
    @Nullable
    private Kit kit;

    public DuelSession(Player playerOne, Player playerTwo) {
        this.players = synchronizedList(playerOne, playerTwo);
        this.data = Maps.newConcurrentMap();
        this.kit = null;
    }

    @Override
    public long getExpireMillis() {
        return getBeginMillis() + Constants.DUEL_PERIOD;
    }

    public DuelSession withKit(Kit kit) {
        this.kit = kit;
        return this;
    }

    public void setWinner(String winner) {
        data.put("winner", winner);
    }

    public String getWinner() {
        return data.getOrDefault("winner", "Nobody");
    }

}
