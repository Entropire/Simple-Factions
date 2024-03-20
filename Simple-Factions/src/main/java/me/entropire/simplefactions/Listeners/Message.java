package me.entropire.simplefactions.Listeners;

import me.entropire.simplefactions.objects.Faction;
import me.entropire.simplefactions.Simple_Factions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.SQLException;
import java.util.ArrayList;

public class Message implements Listener
{

    private final Simple_Factions simpleFactionsPlugin;

    public Message(Simple_Factions simpleFactionsPlugin)
    {
        this.simpleFactionsPlugin = simpleFactionsPlugin;
    }

    @EventHandler
    public void MessageSend(AsyncPlayerChatEvent e)
    {
        try
        {
            Player player = e.getPlayer();
            if(simpleFactionsPlugin.playerDatabase.hasFaction(player))
            {
                String message = e.getMessage();
                int factionId = simpleFactionsPlugin.playerDatabase.getFactionId(player);
                Faction faction = simpleFactionsPlugin.factionDatabase.getFactionDataById(factionId);
                switch (simpleFactionsPlugin.playerDatabase.getChat(player.getUniqueId())){
                    case "public":
                        e.setFormat(faction.color() + "[" + faction.name() + "] " + player.getName() + ": " + ChatColor.GRAY + message);
                        break;
                    case "faction":
                        e.setCancelled(true);
                        handleFactionChat(faction, player, message);
                        break;
                }
            }else{
                e.setFormat(ChatColor.WHITE + player.getName() + ":" + ChatColor.GRAY + e.getMessage());
            }
        }
        catch (SQLException ex)
        {
            throw new RuntimeException(ex);
        }

    }

    private void handleFactionChat(Faction faction, Player player, String message)
    {
        ArrayList<String> members = faction.members();
        for(String s : members) {
            Player member = Bukkit.getPlayer(s);
            if (member != null) {
                member.sendMessage(ChatColor.GREEN + "FACTION: " + faction.color() + "[" + faction.name() + "] " + player.getName() + ": " + ChatColor.GRAY + message);

            }
        }
    }
}
