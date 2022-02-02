package me.ledovec.duels.session;

public class PlayerSessionFactory implements SessionFactory<PlayerSession> {

    public static SessionFactory<PlayerSession> of(String nick) {
        return new PlayerSessionFactory(nick);
    }

    private final String nick;

    private PlayerSessionFactory(String nick) {
        this.nick = nick;
    }

    @Override
    public PlayerSession create() {
        return new PlayerSession(nick);
    }

}
