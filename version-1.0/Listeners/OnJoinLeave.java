package me.entropire.simplefactions.Listeners;

import me.entropire.simplefactions.Simple_Factions;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnJoinLeave implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        if(Simple_Factions.players.containsKey(e.getPlayer().getUniqueId())){
            String factionName = Simple_Factions.players.get(e.getPlayer().getUniqueId());
            ChatColor color = Simple_Factions.factions.get(factionName).color;

            e.getPlayer().setDisplayName(Simple_Factions.factions.get(factionName).color + "[" + Simple_Factions.factions.get(factionName).factionName + "] " + e.getPlayer().getName());
            e.getPlayer().setPlayerListName(Simple_Factions.factions.get(factionName).color + "[" + Simple_Factions.factions.get(factionName).factionName + "] " + e.getPlayer().getName());

            e.setJoinMessage(color + "[" + factionName + "] " + e.getPlayer().getName() + ChatColor.YELLOW + " joined the game");
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e)
    {
        if(Simple_Factions.players.containsKey(e.getPlayer().getUniqueId())){
            String factionName = Simple_Factions.players.get(e.getPlayer().getUniqueId());
            ChatColor color = Simple_Factions.factions.get(factionName).color;

            e.getPlayer().setDisplayName(Simple_Factions.factions.get(factionName).color + "[" + Simple_Factions.factions.get(factionName).factionName + "] " + e.getPlayer().getName());
            e.getPlayer().setPlayerListName(Simple_Factions.factions.get(factionName).color + "[" + Simple_Factions.factions.get(factionName).factionName + "] " + e.getPlayer().getName());

            e.setQuitMessage(color + "[" + factionName + "] " + e.getPlayer().getName() + ChatColor.YELLOW + " left the game");
        }
    }
}
