package me.entropire.simple_factions.database;

import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class PlayerDatabase
{
    private final Connection connection;
    public PlayerDatabase(String path) throws SQLException
    {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);

        try (Statement statement = connection.createStatement())
        {
            statement.execute("""
            CREATE TABLE IF NOT EXISTS Players (
                uuid TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                factionId INTEGER NOT NULL DEFAULT 0,
                chat TEXT NOT NULL DEFAULT public
            )
            """);
        }
    }

    public void closeConnection() throws SQLException
    {
        if(connection != null && !connection.isClosed())
        {
            connection.close();
        }
    }

    public void addPlayer(Player player) throws SQLException
    {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Players (uuid, name) VALUES (?, ?)"))
        {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.execute();
        }
    }

    public boolean playerExists(String playerName) throws SQLException
    {
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Players WHERE name = ?"))
        {
            preparedStatement.setString(1, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public int getFactionId(Player player) throws SQLException
    {
        int factionId = -1;
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT factionId FROM Players WHERE uuid = ?"))
        {
            preparedStatement.setString(1, player.getUniqueId().toString());
            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                if (resultSet.next())
                {
                    factionId = resultSet.getInt("factionId");
                }
            }
        }
        return factionId;
    }

    public void updateFactionWithPlayerUUID(UUID uuid, int factionId)throws SQLException
    {
        try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Players SET factionId = ? WHERE uuid = ?"))
        {
            preparedStatement.setString(1, String.valueOf(factionId));
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        }
    }

    public void updateFactionWithPlayerName(String name, int factionId)throws SQLException
    {
        try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Players SET factionId = ? WHERE name = ?"))
        {
            preparedStatement.setString(1, String.valueOf(factionId));
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
        }
    }

    public boolean hasFaction(Player player) throws SQLException
    {
        boolean hasFaction = false;
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT factionId FROM Players WHERE uuid = ?"))
        {
            preparedStatement.setString(1, player.getUniqueId().toString());
            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                if (resultSet.next())
                {
                    int factionId = resultSet.getInt("factionId");
                    if (factionId > 0)
                    {
                        hasFaction = true;
                    }
                }
            }
        }
        return hasFaction;
    }

    public UUID getPlayerUUID(String playerName) throws SQLException
    {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid FROM Players WHERE name = ?"))
        {
            preparedStatement.setString(1, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
            {
                return UUID.fromString(resultSet.getString("uuid"));
            }
            else
            {
                return null;
            }
        }
    }

    public String getPlayerName(String uuid) throws SQLException
    {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM Players WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("name");
            } else {
                return null;
            }
        }
    }

    public String getChat(UUID uuid)throws SQLException
    {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT chat FROM Players WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("chat");
            } else {
                return null;
            }
        }
    }

    public void setChat(UUID uuid, String chat)throws SQLException
    {
        try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Players SET chat = ? WHERE uuid = ?"))
        {
            preparedStatement.setString(1, chat);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        }
    }
}
