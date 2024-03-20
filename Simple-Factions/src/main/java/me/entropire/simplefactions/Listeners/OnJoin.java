package me.entropire.simplefactions.Listeners;


import me.entropire.simplefactions.objects.Faction;
import me.entropire.simplefactions.Simple_Factions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class OnJoin implements Listener
{
    private final Simple_Factions simpleFactionsPlugin;

    public OnJoin(Simple_Factions simpleFactionsPlugin)
    {
        this.simpleFactionsPlugin = simpleFactionsPlugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        Player player = e.getPlayer();
        try
        {
            if(!simpleFactionsPlugin.playerDatabase.playerExists(player.getName())){
                simpleFactionsPlugin.playerDatabase.addPlayer(e.getPlayer());
            }

            int factionId = simpleFactionsPlugin.playerDatabase.getFactionId(player);
            if(factionId > 1)
            {
                Faction faction = simpleFactionsPlugin.factionDatabase.getFactionDataById(factionId);

                player.setDisplayName(faction.color() + "[" + faction.name() + "] " + player.getName());
                player.setPlayerListName(faction.color() + "[" + faction.name() + "] " + player.getName());
                e.setJoinMessage(faction.color() + "[" + faction.name() + "] " + player.getName() + ChatColor.YELLOW + " joined the game.");
            }
        }
        catch (SQLException ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
