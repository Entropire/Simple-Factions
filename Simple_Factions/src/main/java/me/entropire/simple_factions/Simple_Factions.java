package me.entropire.simple_factions;

import me.entropire.simple_factions.commands.FactionCommand;
import me.entropire.simple_factions.commands.chatCommands;
import me.entropire.simple_factions.database.FactionDatabase;
import me.entropire.simple_factions.database.PlayerDatabase;
import me.entropire.simple_factions.events.Message;
import me.entropire.simple_factions.events.OnInventoryClick;
import me.entropire.simple_factions.events.OnJoin;
import me.entropire.simple_factions.objects.Faction;
import me.entropire.simple_factions.objects.Invite;
import me.entropire.simple_factions.objects.Join;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public final class Simple_Factions extends JavaPlugin
{

    public FactionDatabase factionDatabase;
    public PlayerDatabase playerDatabase;
    public Map<UUID, Faction> createFactions = new HashMap<>();
    public final Map<UUID, Invite> invites = new HashMap<>();
    public final Map<UUID, Join> joins = new HashMap<>();
    @Override
    public void onEnable()
    {
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Simple-Factions loading.");
        this.getServer().getPluginManager().registerEvents(new OnJoin(this), this);
        this.getServer().getPluginManager().registerEvents(new Message(this), this);
        this.getServer().getPluginManager().registerEvents(new OnInventoryClick(), this);
        getCommand("faction").setExecutor(new FactionCommand(this));
        getCommand("chat").setExecutor(new chatCommands(this));
        getCommand("faction").setTabCompleter(new FactionCommand(this));
        getCommand("chat").setTabCompleter(new chatCommands(this));


        try
        {
            if (!getDataFolder().exists()) getDataFolder().mkdir();
            factionDatabase = new FactionDatabase(getDataFolder().getAbsolutePath() + "/Simple-Faction.db");
            playerDatabase = new PlayerDatabase(getDataFolder().getAbsolutePath() + "/Simple-Faction.db");
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            Bukkit.getServer().getConsoleSender().sendMessage("Failed to connect to the database!" + ex.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        Bukkit.getScheduler().runTaskTimer(this, () ->
                {
                    long currentTime = System.currentTimeMillis();
                    invites.entrySet().removeIf(entry -> entry.getValue().expireDate() < currentTime);
                    joins.entrySet().removeIf(entry -> entry.getValue().expireDate() < currentTime);
                }
                , 0L, 20L);
    }

    @Override
    public void onDisable()
    {
        try
        {
            factionDatabase.closeConnection();
            playerDatabase.closeConnection();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }
}
