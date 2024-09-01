package me.entropire.simple_factions.events;


import me.entropire.simple_factions.Simple_Factions;
import me.entropire.simple_factions.objects.Faction;
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
            if(factionId > 0)
            {
                Faction faction = simpleFactionsPlugin.factionDatabase.getFactionDataById(factionId);

                player.setDisplayName(faction.getColor() + "[" + faction.getName() + "] " + player.getName());
                player.setPlayerListName(faction.getColor() + "[" + faction.getName() + "] " + player.getName());
                e.setJoinMessage(faction.getColor() + "[" + faction.getName() + "] " + player.getName() + ChatColor.YELLOW + " joined the game.");
            }
        }
        catch (SQLException ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
