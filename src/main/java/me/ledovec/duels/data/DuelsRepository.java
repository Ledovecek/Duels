package me.ledovec.duels.data;

import me.ledovec.duels.Duels;
import me.ledovec.duels.listeners.DuelEndListener;
import me.ledovec.duels.listeners.DuelStartListener;
import me.ledovec.duels.session.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static me.ledovec.duels.Util.synchronizedList;

public class DuelsRepository {

    private final List<Session> sessions;
    private final List<SessionDataAccessor<?>> accessors;
    private final List<Listener<?>> listeners;
    private final Duels plugin;

    public DuelsRepository(Duels plugin) {
        this.plugin = plugin;
        this.sessions = synchronizedList();
        this.accessors = synchronizedList();
        this.listeners = synchronizedList();
        init();
    }

    @Nullable
    public <T extends Session> T save(SessionFactory<T> factory, Class<T> typeClass) {
        T session = factory.create();
        List<SessionDataAccessor<T>> accessors = getAccessors(typeClass);
        for (SessionDataAccessor<T> accessor : accessors) {
            if(!accessor.modify(session)) {
                return null;
            }
        }
        sessions.add(session);
        invokeListeners(RepoAction.ADDON, typeClass, session);
        return session;
    }

    @SuppressWarnings("unchecked")
    public <T extends Session> boolean remove(Class<T> typeClass, Predicate<T> pred) {
        return sessions.removeIf(session -> {
            if(session.getClass().isAssignableFrom(typeClass) && pred.test((T) session)) {
                invokeListeners(RepoAction.REMOVAL, typeClass, (T) session);
                return true;
            }
            return false;
        });
    }

    public List<DuelRequest> getDuelRequests(String receiverNick) {
        return getSessions(DuelRequest.class, duelRequest -> duelRequest.getReceiver().getName().equals(receiverNick));
    }

    public Optional<PlayerSession> getPlayerSession(String nick) {
        return getSession(PlayerSession.class, playerSession -> playerSession.getNick().equals(nick));
    }

    public Optional<DuelSession> getDuelSession(String nick) {
        return getSession(DuelSession.class, duelSession -> duelSession.getPlayers().stream()
                .anyMatch(p -> p.getName().equals(nick))
        );
    }

    public <T extends Session> Optional<T> getSession(Class<T> typeClass, Predicate<T> pred) {
        return getSessions(typeClass, pred).stream()
                .findFirst();
    }

    @SuppressWarnings("unchecked")
    public <T extends Session> List<T> getSessions(Class<T> typeClass, Predicate<T> pred) {
        sessions.removeIf(session -> session instanceof ExpirableSession && ((ExpirableSession) session).hasExpired());
        return sessions.stream()
                .filter(session -> session.getClass().isAssignableFrom(typeClass) && pred.test((T) session))
                .map(session -> (T) session)
                .collect(Collectors.toList());

    }

    public void registerAccessors(SessionDataAccessor<?>... accessors) {
        registerAccessors(Arrays.stream(accessors).collect(Collectors.toList()));
    }

    public void registerAccessors(List<SessionDataAccessor<?>> accessors) {
        accessors.forEach(this::registerAccessor);
    }

    public void registerAccessor(SessionDataAccessor<?> accessor) {
        accessors.add(accessor);
    }

    public void registerListener(Listener<?> listener) {
        listeners.add(listener);
    }

    public boolean reload() {
        sessions.clear();
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(save(PlayerSessionFactory.of(p.getName()), PlayerSession.class) == null) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T extends Session> List<SessionDataAccessor<T>> getAccessors(Class<T> typeClass) {
        return accessors.stream()
                .filter(accessor -> accessor.getTargetObjectType().isAssignableFrom(typeClass))
                .map(accessor -> (SessionDataAccessor<T>) accessor)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private <T extends Session> boolean invokeListeners(@NotNull RepoAction action, @NotNull Class<T> targetTypeClass, T target) {
        for (Listener<?> listener : listeners.stream()
                .filter(l -> l.getAction().equals(action) && l.getTargetType().isAssignableFrom(targetTypeClass))
                .collect(Collectors.toList())) {
            if(!((Listener<T>) listener).invoke(target)) {
                return false;
            }
        }
        return true;
    }

    private void init() {
        registerAccessor(new RandomKitAccessor(plugin.getKitsRepository()));
        registerListener(new DuelStartListener());
        registerListener(new DuelEndListener(plugin.getMariaDB()));
    }

}
