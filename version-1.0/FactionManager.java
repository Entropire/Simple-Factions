package me.entropire.simplefactions;

import me.entropire.simplefactions.Libraries.Colors;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.awt.Color.YELLOW;
import static me.entropire.simplefactions.Simple_Factions.*;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public class FactionManager {
    Colors colors = new Colors();

    public void createFaction(Player player, String factionName, ChatColor color)
    {
        if(players.containsKey(player.getUniqueId())){ player.sendMessage(RED + "You are already in a faction"); return; }
        if(factions.containsKey(factionName)){ player.sendMessage(RED + "There already is a faction with the name '" + factionName + "'"); return;}
        Faction faction = new Faction(factionName, player.getUniqueId());
        if(color != null){ faction.setColor(color); }
        factions.put(factionName, faction);
        players.put(player.getUniqueId(), factionName);
        player.setDisplayName(faction.color + "[" + faction.factionName + "] " + player.getName());
        player.setPlayerListName(faction.color + "[" + faction.factionName + "] " + player.getName());
        player.sendMessage(GREEN + "New faction " + factionName + " created");
    }

    public void deleteFaction(Player player, String factionName)
    {
        if(!factions.containsKey(factionName)){ player.sendMessage(RED + "There is no faction called '" + factionName + "'"); return;}
        if(!players.get(player.getUniqueId()).equals(factionName)){ player.sendMessage(RED + "You can not delete a faction you're not in"); }
        if(!factions.get(factionName).owner.equals(player.getUniqueId())){ player.sendMessage(RED + "You need to be the owner of the faction to delete it"); return;}
        for(Map.Entry<UUID, String> entry : factions.get(factionName).members.entrySet()){
            Player member = Bukkit.getPlayer(entry.getKey());
            if(member != null){
                players.remove(member.getUniqueId());
                member.setDisplayName(player.getName());
                member.setPlayerListName(player.getName());
            }
        }
        factions.remove(factionName);
        player.sendMessage(RED + "Faction " + factionName + " deleted");
    }

    public void factionInvite(Player player, String invitedPlayerName)
    {
        if(!players.containsKey(player.getUniqueId())){ player.sendMessage(RED + "You must be in a faction to invite somebody"); return;}
        Player invitedPlayer = Bukkit.getPlayer(invitedPlayerName);
        Faction faction = factions.get(players.get(player.getUniqueId()));
        if (invitedPlayer == null) { player.sendMessage(RED + "Could not find player '" + invitedPlayerName + "'"); return;  }
        if(players.containsKey(invitedPlayer.getUniqueId())){ player.sendMessage(RED + "Player is already in a faction"); return;}
        if(!faction.owner.equals(player.getUniqueId())){ player.sendMessage(RED + "Only the owner of the faction can invite players"); return; }
        Object[] inviteData = new Object[2];
        inviteData[0] = player;
        inviteData[1] = faction;
        pendingInvites.put(invitedPlayer.getUniqueId(), inviteData);
        ComponentBuilder builder = new ComponentBuilder("You have been invited to faction " + faction.factionName)
                .append("Do you want to join this faction ")
                .append("[Accept]").color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction accept"))
                .append(" | ").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("[Decline]").color(net.md_5.bungee.api.ChatColor.RED)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction decline"));

        invitedPlayer.spigot().sendMessage(builder.create());
    }

    public void leaveFaction(Player player)
    {
        if(!players.containsKey(player.getUniqueId())){ player.sendMessage(RED + "You must be in a faction to leave a faction"); return;}
        Faction leaveFaction = factions.get(players.get(player.getUniqueId()));
        if(leaveFaction.owner == player.getUniqueId()){ player.sendMessage(RED + "The owner of the faction can not leave the faction"); return;}
        leaveFaction.members.remove(player.getUniqueId());
        players.remove(player.getUniqueId());

        player.setDisplayName(player.getName());
        player.setPlayerListName(player.getName());

        player.sendMessage(GREEN + "You left the faction " + leaveFaction.factionName);
    }

    public void kickMember(Player player, String member)
    {
        Player removeMember = Bukkit.getPlayer(member);
        Faction faction = factions.get(players.get(player.getUniqueId()));
        if (removeMember == null) { player.sendMessage(RED + "Could not find player '" + member + "'"); return;  }
        if(!faction.members.containsKey(removeMember.getUniqueId())){ player.sendMessage(RED + "Player is not in your faction"); return;}
        if(faction.owner != player.getUniqueId()){ player.sendMessage(RED + "Only the owner of the faction can remove a member"); return;}
        if(faction.owner == removeMember.getUniqueId()){ player.sendMessage(RED + "The Owner of the faction can not be kicked"); return; }
        faction.removeMember(removeMember.getUniqueId());
        players.remove(removeMember.getUniqueId());

        removeMember.setDisplayName(removeMember.getName());
        removeMember.setPlayerListName(removeMember.getName());

        player.sendMessage(GREEN + "Removed " + removeMember.getName() + " from faction " + faction.factionName);
    }

    public void joinFaction(Player player, String factionName)
    {
        if(players.containsKey(player.getUniqueId())){ player.sendMessage(RED + "You are already in a faction"); return;}
        Faction faction = factions.get(factionName);
        Player owner = Bukkit.getPlayer(faction.owner);
        if(owner == null){ player.sendMessage(RED + "Somthing went rong with the join reqeust"); return;}
        if(!owner.isOnline()){ player.sendMessage(RED + "Owner of faction is not online"); return; }
        joinRequests.put(faction.owner, player.getUniqueId());
        ComponentBuilder builder = new ComponentBuilder( player.getName() + " requested to join your faction ").color(net.md_5.bungee.api.ChatColor.YELLOW)
                .append("[Accept]").color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction accept"))
                .append(" | ").color(net.md_5.bungee.api.ChatColor.WHITE)
                .append("[Decline]").color(net.md_5.bungee.api.ChatColor.RED)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction decline"));

        owner.sendMessage(builder.create());
    }

    public  void factionSetName(Player player, String newFactionName)
    {
        if(factions.containsKey(newFactionName)){ player.sendMessage(RED + "There is already a faction with this name!"); return;}
        String factionName = players.get(player.getUniqueId());
        Faction faction = factions.get(factionName);
        if(!faction.owner.equals(player.getUniqueId())){ player.sendMessage(RED + "Only the owner of the faction can change the faction name "); return;}
        replaceStringInKeys(factionName, newFactionName);
        replaceStringInValues(factionName, newFactionName);
        faction.setFactionName(newFactionName);

        for(Map.Entry<UUID, String> entry : faction.members.entrySet()) {
            Player member = Bukkit.getPlayer(entry.getKey());

            if (member != null) {
                member.setDisplayName(faction.color + "[" + newFactionName + "] " + player.getName());
                member.setPlayerListName(faction.color + "[" + newFactionName + "] " + player.getName());
            }
        }

        player.sendMessage(GREEN + "Changed faction name '" + factionName +"' to '" + newFactionName + "'");
    }

    public void factionSetColor(Player player, String newColor, boolean uses)
    {
        ChatColor color = colors.getChatColorWithColorName(ChatColor.stripColor(newColor).toLowerCase());

        if(color == null){
            player.sendMessage("available colors: ");
            player.sendMessage(ChatColor.BLACK + "black");
            player.sendMessage(ChatColor.RED + "red");
            player.sendMessage(ChatColor.AQUA + "aqua");
            player.sendMessage(ChatColor.BLUE + "blue");
            player.sendMessage(ChatColor.DARK_AQUA + "dark_aqua");
            player.sendMessage(ChatColor.DARK_BLUE + "dark_blue");
            player.sendMessage(ChatColor.DARK_GRAY + "dark_gray");
            player.sendMessage(ChatColor.DARK_GREEN + "dark_green");
            player.sendMessage(ChatColor.DARK_PURPLE + "dark_purple");
            player.sendMessage(ChatColor.DARK_RED + "dark_red");
            player.sendMessage(ChatColor.GOLD + "gold");
            player.sendMessage(ChatColor.GRAY + "gray");
            player.sendMessage(ChatColor.GREEN + "green");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "light_purple");
            player.sendMessage(ChatColor.WHITE + "white");
            player.sendMessage(ChatColor.YELLOW + "yellow");
            return;
        }

        if(uses){
            Faction faction = createFactions.get(player.getUniqueId());
            faction.setColor(color);
            return;

        }

        String factionName = players.get(player.getUniqueId());
        Faction faction = factions.get(factionName);
        faction.setColor(color);

        for(Map.Entry<UUID, String> entry : factions.get(factionName).members.entrySet()) {
            Player member = Bukkit.getPlayer(entry.getKey());

            if (member != null) {
                member.setDisplayName(faction.color + "[" + factionName + "] " + player.getName());
                member.setPlayerListName(faction.color + "[" + factionName + "] " + player.getName());
            }
        }

        player.sendMessage(ChatColor.GREEN + "Changed faction color to '" + color + newColor + "'");
    }

    public void factionSetOwner(Player player, String newOwnerName)
    {
        Faction faction = factions.get(players.get(player.getUniqueId()));
        Player newOwner = Bukkit.getPlayer(newOwnerName);
        if(newOwner == null){ player.sendMessage(RED + "Could not find player '" + newOwnerName + "'"); return; }
        if(!faction.members.containsKey(newOwner.getUniqueId())){ player.sendMessage(ChatColor.RED + "Player must be in your faction to make him owner"); }
        faction.setOwner(player, newOwner.getUniqueId());
        player.sendMessage(GREEN + "You made '" + newOwnerName + "' owner of '" + faction.factionName + "'");
    }

    public static void replaceStringInValues(String oldString, String newString)
    {
        HashMap<UUID, String> updatedPlayers = new HashMap<>();

        for (UUID key : players.keySet()) {
            String value = players.get(key);
            String updatedValue = value.replace(oldString, newString);
            updatedPlayers.put(key, updatedValue);
        }
        players = updatedPlayers;
    }

    public static void replaceStringInKeys(String oldString, String newString)
    {
        HashMap<String, Faction> updatedFactions = new HashMap<>();

        for (String key : factions.keySet()) {
            String updatedKey = key.replace(oldString, newString);
            Faction value = factions.get(key);
            updatedFactions.put(updatedKey, value);
        }
        factions = updatedFactions;
    }
}