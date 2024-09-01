package me.entropire.simple_factions.database;

import me.entropire.simple_factions.objects.Colors;
import me.entropire.simple_factions.objects.Faction;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class FactionDatabase
{

    private final Colors colors = new Colors();

    private final Connection connection;
    public FactionDatabase(String path) throws SQLException
    {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);

        try (Statement statement = connection.createStatement())
        {
            statement.execute("""
            CREATE TABLE IF NOT EXISTS Factions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                color TEXT NOT NULL,
                owner TEXT NOT NULL,
                members TEXT NOT NULL
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

    public void addFaction(Faction faction) throws SQLException
    {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Factions (name, color, owner, members) VALUES (?, ?, ?, ?)"))
        {
            preparedStatement.setString(1, faction.getName());
            preparedStatement.setString(2, colors.getColorNameWithChatColor(faction.getColor()));
            preparedStatement.setString(3, faction.getOwner().toString());
            preparedStatement.setString(4, String.join(",", faction.getMembers()));
            preparedStatement.execute();
        }
    }

    public boolean factionExistsByName(String factionName) throws SQLException
    {
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Factions WHERE name = ?"))
        {
            preparedStatement.setString(1, factionName);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public void updateFactionName(int factionId, String newName)throws SQLException
    {
        try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Factions SET name = ? WHERE id = ?"))
        {
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, String.valueOf(factionId));
            preparedStatement.executeUpdate();
        }
    }

    public void updateFactionColor(int factionId, String newColor)throws SQLException
    {
        try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Factions SET color = ? WHERE id = ?"))
        {
            preparedStatement.setString(1, newColor);
            preparedStatement.setString(2, String.valueOf(factionId));
            preparedStatement.executeUpdate();
        }
    }

    public void updateFactionOwner(int factionId, String newOwnerUUid)throws SQLException
    {
        try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Factions SET owner = ? WHERE id = ?"))
        {
            preparedStatement.setString(1, newOwnerUUid);
            preparedStatement.setString(2, String.valueOf(factionId));
            preparedStatement.executeUpdate();
        }
    }

    public void updateFactionMembers(int factionId, String member, Boolean add)throws SQLException
    {
        ArrayList<String> membersList;
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT members FROM Factions WHERE id = ?")) {
            preparedStatement.setString(1, String.valueOf(factionId));
            ResultSet resultSet = preparedStatement.executeQuery();
            membersList = new ArrayList<>(Arrays.asList(resultSet.getString("members").split(",")));

            if(add)
            {
                membersList.add(member);
            }
            else
            {
                membersList.remove(member);
            }
        }

        try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Factions SET members = ? WHERE id = ?"))
        {
            preparedStatement.setString(1, String.join(",", membersList));
            preparedStatement.setString(2, String.valueOf(factionId));
            preparedStatement.executeUpdate();
        }
    }

    public Faction getFactionDataById(int factionId)throws SQLException
    {
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Factions WHERE id = ?"))
        {
            preparedStatement.setString(1, String.valueOf(factionId));
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
            {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String color = resultSet.getString("color");
                String owner = resultSet.getString("owner");

                String membersString = resultSet.getString("members");
                ArrayList<String> membersList = new ArrayList<>(Arrays.asList(membersString.split(",")));

                return new Faction(id, name, colors.getChatColorWithColorName(color), UUID.fromString(owner), membersList);
            }
        }
        return null;
    }

    public Faction getFactionDataByName(String factionName)throws SQLException
    {
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Factions WHERE name = ?"))
        {
            preparedStatement.setString(1, factionName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
            {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String color = resultSet.getString("color");
                String owner = resultSet.getString("owner");

                String membersString = resultSet.getString("members");
                ArrayList<String> membersList = new ArrayList<>(Arrays.asList(membersString.split(",")));

                return new Faction(id, name, colors.getChatColorWithColorName(color), UUID.fromString(owner), membersList);
            }
        }
        return null;
    }

    public ArrayList<String> getFactions() throws SQLException
    {
        ArrayList<String> factionNames = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Factions"))
        {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                String name = resultSet.getString("name");
                factionNames.add(name);
            }
        }
        return factionNames;
    }

    public void deleteFaction(int factionId) throws SQLException
    {
        try(PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Factions WHERE id = ?"))
        {
            preparedStatement.setString(1, String.valueOf(factionId));
            preparedStatement.executeUpdate();
        }
    }
}