package me.ledovec.duels.database;

import lombok.SneakyThrows;
import me.ledovec.duels.Duels;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DatabaseController implements Database<Player, Integer> {

    private MariaDB mariaDB = Duels.INSTANCE.getMariaDB();
    private final Duels plugin;

    public DatabaseController(Duels duels) {
        this.plugin = duels;
    }

    @Override
    public void createTable() {
        mariaDB.exec("CREATE TABLE IF NOT EXISTS Stats (nick VARCHAR(255), wins INT, loses INT, kills INT, deaths INT);");
    }

    @Override
    public void create(Player p) {
        create(p.getName());
    }

    @SneakyThrows
    public void create(String nick) {
        ResultSet resultSet = mariaDB.execQuery("SELECT COUNT(*) FROM Stats WHERE nick='" + nick + "';");
        resultSet.next();
        int anInt = resultSet.getInt(1);
        if (anInt == 0) {
            mariaDB.exec(
                    "INSERT INTO Stats (nick, wins, loses, kills, deaths) VALUES ('"
                            + nick + "', 0, 0, 0, 0);"
            );
        }
        resultSet.close();
    }

    @SneakyThrows
    @Override
    public void update(Player p, HashMap<String, Integer> values) {
        PreparedStatement prepareStatement = mariaDB.getConnection().prepareStatement("UPDATE Stats SET kills=?, deaths=?, wins=?, loses=? WHERE nick='" + p.getName() + "';");
        Integer kills = values.get("kills");
        Integer deaths = values.get("deaths");
        Integer wins = values.get("wins");
        Integer loses = values.get("loses");
        prepareStatement.setInt(1, kills);
        prepareStatement.setInt(2, deaths);
        prepareStatement.setInt(3, wins);
        prepareStatement.setInt(4, loses);
        prepareStatement.execute();
        prepareStatement.close();
    }

    @SneakyThrows
    @Override
    public List<Integer> get(Player p) {
        ResultSet resultSet = mariaDB.execQuery("SELECT * FROM Stats WHERE nick='" + p.getName() + "';");
        if (resultSet != null) {
            if(resultSet.next())  {
                int kills = resultSet.getInt("kills");
                int deaths = resultSet.getInt("deaths");
                int wins = resultSet.getInt("wins");
                int loses = resultSet.getInt("loses");
                List<Integer> list = new ArrayList<>(Arrays.asList(kills, deaths, wins, loses));
                resultSet.close();
                return list;
            }
        }
        return null;
    }

}
