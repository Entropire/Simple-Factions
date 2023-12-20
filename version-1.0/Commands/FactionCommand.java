package me.entropire.simplefactions.Commands;

import me.entropire.simplefactions.Faction;
import me.entropire.simplefactions.FactionManager;
import me.entropire.simplefactions.GUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

import static me.entropire.simplefactions.Simple_Factions.*;

public class FactionCommand implements CommandExecutor{

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player)){ commandSender.sendMessage(ChatColor.RED + "Only players can use this command"); return false;}
        Player sender = (Player) commandSender;
        FactionManager factionManager = new FactionManager();
        GUI gui = new GUI();
        if(args.length == 0){
            if(players.containsKey(sender.getUniqueId())){
                gui.faction(sender);
                return false;
            }
            if(createFactions.containsKey(sender.getUniqueId())){
                gui.createFaction(sender);
                return false;
            }
            gui.noFaction(sender);
            return false;
        }

        switch (args[0].toLowerCase()){
            case "create":
                if(args.length < 2){ sender.sendMessage(ChatColor.RED + "/faction create [faction name]"); return false;}
                factionManager.createFaction(sender, args[1], null);
                break;
            case "delete":
                if(args.length < 2){ sender.sendMessage(ChatColor.RED + "/faction delete [faction name]"); return false;}
                factionManager.deleteFaction(sender, args[1]);
                break;
            case "list":
                sender.sendMessage("Factions:");
                for(Map.Entry<String, Faction> entry : factions.entrySet()){
                    sender.sendMessage("- " + entry.getKey());
                }
                break;
            case "members":
                if(args.length < 2){
                    String factionName = players.get(sender.getUniqueId());
                    sender.sendMessage("Members of faction " + factionName + ":");
                    for(Map.Entry<UUID, String> entry : factions.get(factionName).members.entrySet()){
                        sender.sendMessage("- " + getPlayerNameFromUUID(entry.getKey().toString()));
                    }
                    return false;
                }
                if(!factions.containsKey(args[1])){ sender.sendMessage(ChatColor.RED + "There is no faction called " + args[1]); return false;}
                sender.sendMessage("Members of faction " + args[1] + ":");
                for(Map.Entry<UUID, String> entry : factions.get(args[1]).members.entrySet()){
                    sender.sendMessage("- " + getPlayerNameFromUUID(entry.getKey().toString()));
                }
                break;
            case "owner":
                if(args.length < 2){
                    String factionName = players.get(sender.getUniqueId());
                    sender.sendMessage("Owner of " + factionName + ": " + getPlayerNameFromUUID(factions.get(factionName).owner.toString()));
                    return false;
                }
                if(!factions.containsKey(args[1])){ sender.sendMessage(ChatColor.RED + "There is no faction called " + args[1]); return false;}
                sender.sendMessage("Owner of " + args[1] + ": " + getPlayerNameFromUUID(factions.get(args[1]).owner.toString()));
                break;
            case "invite":
                if(args.length < 2){ sender.sendMessage(ChatColor.RED + "/faction invite [player name]"); return false;}
                factionManager.factionInvite(sender, args[1]);
                break;
            case "accept":
                if(pendingInvites.containsKey(sender.getUniqueId()))
                {
                    Object[] invitationData = pendingInvites.get(sender.getUniqueId());
                    Object invitationAcceptPlayerObject = invitationData[0];
                    Player invitationAcceptPlayer = (Player) invitationAcceptPlayerObject;
                    Object invitationAcceptFactionObject = invitationData[1];
                    Faction invitationAcceptFaction = (Faction) invitationAcceptFactionObject;
                    invitationAcceptFaction.addMember(sender.getUniqueId());
                    players.put(sender.getUniqueId(), invitationAcceptFaction.factionName);
                    sender.sendMessage(ChatColor.GREEN + "you joined faction " + invitationAcceptFaction.factionName);
                    invitationAcceptPlayer.sendMessage(ChatColor.GREEN + sender.getName() + " joined your faction");
                    sender.setDisplayName(factions.get(invitationAcceptFaction.factionName).color + "[" + invitationAcceptFaction.factionName + "] " + sender.getName());
                    sender.setPlayerListName(factions.get(invitationAcceptFaction.factionName).color + "[" + invitationAcceptFaction.factionName + "] " + sender.getName());
                    pendingInvites.remove(sender.getUniqueId());
                    return false;
                }

                if(joinRequests.containsKey(sender.getUniqueId())){
                    UUID playerUUID = joinRequests.get(sender.getUniqueId());
                    Player player = Bukkit.getPlayer(playerUUID);
                    if(player == null){ sender.sendMessage("Sender of join request is not a player"); return false; }
                    Faction faction = factions.get(players.get(sender.getUniqueId()));
                    faction.addMember(sender.getUniqueId());
                    players.put(playerUUID, faction.factionName);
                    sender.sendMessage(ChatColor.GREEN + getPlayerNameFromUUID(playerUUID.toString()) + "joined your faction");
                    player.sendMessage(ChatColor.GREEN + "You joined faction " + faction.factionName);
                    player.setDisplayName(factions.get(faction.factionName).color + "[" + faction.factionName + "] " + player.getName());
                    player.setPlayerListName(factions.get(faction.factionName).color + "[" + faction.factionName + "] " + player.getName());
                    joinRequests.remove(sender.getUniqueId());
                    return false;
                }
                sender.sendMessage(ChatColor.RED + "You do not have a pending faction invitation or a pending join reqeust");


                break;
            case "decline":
                if(pendingInvites.containsKey(sender.getUniqueId()))
                {
                    Object[] invitationDeclineData = pendingInvites.get(sender.getUniqueId());
                    Object invitationDeclinePlayerObject = invitationDeclineData[0];
                    Player invitationDeclinePlayer = (Player) invitationDeclinePlayerObject;

                    sender.sendMessage(ChatColor.YELLOW + "You have refused the join reqeust");
                    invitationDeclinePlayer.sendMessage(ChatColor.YELLOW + "Player " + sender.getName() + " declined you join request");

                    joinRequests.remove(sender.getUniqueId());
                    return false;
                }

                if(joinRequests.containsKey(sender.getUniqueId())){
                    UUID playerUUID = joinRequests.get(sender.getUniqueId());
                    Player player = Bukkit.getPlayer(playerUUID);
                    if(player == null){ sender.sendMessage("Sender of join request is not a player"); return false; }

                    sender.sendMessage(ChatColor.YELLOW + "You have refused the invitation");
                    player.sendMessage(ChatColor.YELLOW + "Player " + sender.getName() + " did not want to join your faction");

                    pendingInvites.remove(sender.getUniqueId());
                    return false;
                }
                sender.sendMessage(ChatColor.RED + "You do not have a pending faction invitation or a pending join reqeust");

                break;
            case "leave":
                factionManager.leaveFaction(sender);
                break;
            case "kick":
                if(args.length < 2){ sender.sendMessage(ChatColor.RED + "/faction removeMember [Player name]"); return false; }
                factionManager.kickMember(sender, args[1]);
                break;
            case "modify":
                String factionName = players.get(sender.getUniqueId());
                if(!factions.get(factionName).members.containsKey(sender.getUniqueId())){ sender.sendMessage(ChatColor.RED + "U need to be in a faction to modify it"); return false;}
                if(!factions.get(players.get(sender.getUniqueId())).owner.equals(sender.getUniqueId())){ sender.sendMessage(ChatColor.RED + "U need to be the owner of the faction to change it"); return false; }

                if(args.length < 2){
                    sender.sendMessage(ChatColor.RED + "Invalid  Command:");
                    sender.sendMessage(ChatColor.RED + "/faction modify [subcommand]");
                    sender.sendMessage("Available subcommands:");
                    sender.sendMessage("- name");
                    sender.sendMessage("- color");
                    sender.sendMessage("- owner");

                    return false;
                }
                switch (args[1].toLowerCase()){
                    case "name":
                        if(args.length < 3){ sender.sendMessage(ChatColor.RED + "/faction modify name [new faction name]"); return false; }
                        factionManager.factionSetName(sender, args[2]);
                        break;
                    case "color":
                        if(args.length < 3){ sender.sendMessage(ChatColor.RED + "/faction modify color [minecraft color name]"); return false; }
                        factionManager.factionSetColor(sender, args[2], false);
                        break;
                    case "owner":
                        if(args.length < 3){ sender.sendMessage(ChatColor.RED + "/faction modify owner [new owner]"); return false; }
                        factionManager.factionSetOwner(sender, args[2]);
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED + "Invalid  Command:");
                        sender.sendMessage(ChatColor.RED + "/faction modify [subcommand]");
                        sender.sendMessage("Available subcommands:");
                        sender.sendMessage("- name");
                        sender.sendMessage("- color");
                        sender.sendMessage("- owner");
                }
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Invalid  Command:");
                sender.sendMessage(ChatColor.RED + "/faction [subcommand]");
                sender.sendMessage("Available subcommands:");
                sender.sendMessage("- create");
                sender.sendMessage("- delete");
                sender.sendMessage("- list");
                sender.sendMessage("- modify");
                sender.sendMessage("- members");
                sender.sendMessage("- owner");
                sender.sendMessage("- invite");
                sender.sendMessage("- leave");
                sender.sendMessage("- removemember");

                return false;
        }

        return false;
    }
    public String getPlayerNameFromUUID(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);

        // Retrieve player by UUID
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            return player.getName();
        } else {
            return "Unknown Player";
        }
    }
}