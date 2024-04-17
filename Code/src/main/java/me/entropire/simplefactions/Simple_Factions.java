package me.entropire.simplefactions;

import me.entropire.simplefactions.Listeners.Message;
import me.entropire.simplefactions.Listeners.OnInventoryClick;
import me.entropire.simplefactions.Listeners.OnJoin;
import me.entropire.simplefactions.commands.FactionCommand;
import me.entropire.simplefactions.commands.chatCommands;
import me.entropire.simplefactions.database.FactionDatabase;
import me.entropire.simplefactions.database.PlayerDatabase;
import me.entropire.simplefactions.objects.Faction;
import me.entropire.simplefactions.objects.Invite;
import me.entropire.simplefactions.objects.Join;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("ALL")
public final class Simple_Factions extends JavaPlugin
{

    public FactionDatabase factionDatabase;
    public PlayerDatabase playerDatabase;
    public final Map<UUID, Invite> invites = new HashMap<>();
    public final Map<UUID, Join> joins = new HashMap<>();
    @Override
    public void onEnable()
    {
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Simple-Factions loading.");
        this.getServer().getPluginManager().registerEvents(new OnJoin(this), this);
        this.getServer().getPluginManager().registerEvents(new Message(this), this);
        this.getServer().getPluginManager().registerEvents(new OnInventoryClick(this), this);
        getCommand("faction").setExecutor(new FactionCommand(this));
        getCommand("chat").setExecutor(new chatCommands(this));

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
