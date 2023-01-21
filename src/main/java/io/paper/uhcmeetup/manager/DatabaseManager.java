package io.paper.uhcmeetup.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import io.paper.uhcmeetup.Game;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class DatabaseManager {
    private Game game = Game.getInstance();
    private Connection connection;
    private String host = this.game.getConfig().getString("MYSQL.HOST");
    private String database = this.game.getConfig().getString("MYSQL.DATABASE");
    private String username = this.game.getConfig().getString("MYSQL.USERNAME");
    private String password = this.game.getConfig().getString("MYSQL.PASSWORD");
    private int port = this.game.getConfig().getInt("MYSQL.PORT");

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void connectToDatabase() throws ClassNotFoundException, SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            return;
        }
        DatabaseManager databaseManager = this;
        synchronized (databaseManager) {
            if (this.connection != null && !this.connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.username, this.password);
        }
    }

    public void disconnectFromDatabase() throws SQLException {
        if (!this.connection.isClosed()) {
            this.connection.close();
        }
    }

    public void createTable() throws SQLException {
        Statement statement = this.connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS STATS(USERNAME VARCHAR(100), UUID VARCHAR(100), KILLS VARCHAR(100), DEATHS VARCHAR(100), WINS VARCHAR(100))");
    }

    public boolean isPlayerRegistered(OfflinePlayer player) {
        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM STATS WHERE UUID=?");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void registerPlayer(Player player) {
        try {
            if (!this.isPlayerRegistered(player)) {
                PreparedStatement statement = this.connection.prepareStatement("INSERT INTO STATS(USERNAME, UUID, KILLS, DEATHS, WINS) VALUE (?,?,?,?,?)");
                statement.setString(1, player.getName());
                statement.setString(2, player.getUniqueId().toString());
                statement.setInt(3, 0);
                statement.setInt(4, 0);
                statement.setInt(5, 0);
                statement.executeUpdate();
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int getKills(OfflinePlayer player) {
        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM STATS WHERE UUID=?");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("KILLS");
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public int getDeaths(OfflinePlayer player) {
        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM STATS WHERE UUID=?");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("DEATHS");
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public int getWins(OfflinePlayer player) {
        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM STATS WHERE UUID=?");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("WINS");
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public void addWins(Player player, int wins) {
        try {
            PreparedStatement statement = this.connection.prepareStatement("UPDATE STATS SET WINS=? WHERE UUID=?");
            statement.setInt(1, this.getWins(player) + wins);
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void addKills(Player player, int kills) {
        try {
            PreparedStatement statement = this.connection.prepareStatement("UPDATE STATS SET KILLS=? WHERE UUID=?");
            statement.setInt(1, this.getKills(player) + kills);
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void addDeaths(Player player, int deaths) {
        try {
            PreparedStatement statement = this.connection.prepareStatement("UPDATE STATS SET DEATHS=? WHERE UUID=?");
            statement.setInt(1, this.getDeaths(player) + deaths);
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void removeDeaths(Player player, int deaths) {
        try {
            PreparedStatement statement = this.connection.prepareStatement("UPDATE STATS SET DEATHS=? WHERE UUID=?");
            statement.setInt(1, this.getDeaths(player) - deaths);
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
