package me.entropire.simplefactions.Listeners;

import me.entropire.simplefactions.Faction;
import me.entropire.simplefactions.FactionManager;
import me.entropire.simplefactions.GUI;
import me.entropire.simplefactions.Simple_Factions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

import static me.entropire.simplefactions.Simple_Factions.*;

public class ChatUse implements Listener {
    @EventHandler
    public void onMassageSend(PlayerChatEvent e){
        if(Simple_Factions.players.containsKey(e.getPlayer().getUniqueId()))
        {
            Player player = e.getPlayer();
            String factionName = Simple_Factions.players.get(player.getUniqueId());
            ChatColor color = Simple_Factions.factions.get(factionName).color;

            e.setFormat(color + "[" + factionName + "] " + e.getPlayer().getName() + ChatColor.WHITE + ": " + e.getMessage());
            return;
        }

        e.setFormat(ChatColor.WHITE + e.getPlayer().getName() + ": " + e.getMessage());
    }

    @EventHandler
    public void waitForMessage(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        String message = e.getMessage();
        FactionManager factionManager = new FactionManager();
        if (namingFactions.containsKey(player.getUniqueId()))
        {
            namingFactions.remove(player.getUniqueId());
            e.setCancelled(true);
            if(createFactions.containsKey(player.getUniqueId()))
            {
                Faction faction = createFactions.get(player.getUniqueId());
                if(factions.containsKey(message)) { player.sendMessage(ChatColor.RED + "There is already a faction called " + message); return;}
                faction.setFactionName(message);
                player.sendMessage(ChatColor.GREEN + "Changed faction name to " + message);
            }
            if(factions.containsKey(players.get(player.getUniqueId())))
            {
                factionManager.factionSetName(e.getPlayer(), message);
            }
        }
    }
}
