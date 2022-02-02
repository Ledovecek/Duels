package me.ledovec.duels.data;

import me.ledovec.duels.session.DuelSession;

import java.util.Optional;

public class RandomKitAccessor extends DuelDataAccessor {

    private final KitsRepository kitsRepository;

    public RandomKitAccessor(KitsRepository kitsRepository) {
        this.kitsRepository = kitsRepository;
    }

    @Override
    public boolean modify(DuelSession duelSession) {
        if(duelSession.getKit() == null) {
            Optional<Kit> randomKit = kitsRepository.getRandomKit();
            randomKit.ifPresent(duelSession::withKit);
        }
        return true;
    }

}
