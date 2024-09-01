package me.entropire.simple_factions.commands;

import me.entropire.simple_factions.FactionManager;
import me.entropire.simple_factions.Gui;
import me.entropire.simple_factions.Simple_Factions;
import me.entropire.simple_factions.objects.Faction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FactionCommand implements CommandExecutor, TabCompleter
{
    Simple_Factions simpleFactionsPlugin;
    FactionManager factionManager;
    Gui gui;

    public FactionCommand(Simple_Factions simpleFactionsPlugin)
    {
        factionManager = new FactionManager(simpleFactionsPlugin);
        gui = new Gui(simpleFactionsPlugin);
        this.simpleFactionsPlugin = simpleFactionsPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if (!(sender instanceof Player player))
        {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return false;
        }

        if(args.length == 0)
        {
            try {
                if(simpleFactionsPlugin.playerDatabase.hasFaction(player))
                {
                    int factionId = simpleFactionsPlugin.playerDatabase.getFactionId(player);
                    Faction faction = simpleFactionsPlugin.factionDatabase.getFactionDataById(factionId);
                    gui.Faction(player, faction);
                    return true;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if(simpleFactionsPlugin.createFactions.containsKey(player.getUniqueId()))
            {
               gui.CreateFaction(player);
            }
            else
            {
                gui.SimpleFaction(player);
            }

            return false;
        }

        switch (args[0].toLowerCase())
        {
            case "create":
                handleCreateCommand(args, player);
                break;
            case "delete":
                handleDeleteCommand(player);
                break;
            case "list":
                handleListCommand(player);
                break;
            case "members":
                handleMembersCommand(args, player);
                break;
            case "owner":
                handleOwnerCommand(args, player);
                break;
            case "leave":
                handleLeaveCommand(player);
                break;
            case "kick":
                handleKickCommand(args, player);
                break;
            case "invite":
                handleInviteCommand(args, player);
                break;
            case "accept":
                handleAcceptCommand(player);
                break;
            case "decline":
                handleDeclineCommand(player);
                break;
            case "join":
                handleJoinCommand(args, player);
                break;
            case "modify":
                handleModifyCommand(args, player);
                break;
            case "now":
                handleNowCommand(args, player); //should be removed in the released version
                break;
            default:
                player.sendMessage("Available commands for factions are");
                player.sendMessage("/faction create");
                player.sendMessage("/faction delete");
                player.sendMessage("/faction list");
                player.sendMessage("/faction owner");
                player.sendMessage("/faction members");
                player.sendMessage("/faction leave");
                player.sendMessage("/faction kick");
                player.sendMessage("/faction invite");
                player.sendMessage("/faction join");
                player.sendMessage("/faction accept");
                player.sendMessage("/faction decline");
                player.sendMessage("/faction modify");
                break;
        }
        return false;
    }

    private void handleCreateCommand(String[] args, Player player)
    {
        if (args.length < 2)
        {
            player.sendMessage(ChatColor.RED + "/faction create [faction name].");
            return;
        }

        try
        {
            factionManager.create(player, args[1]);
        }
        catch (SQLException ex)
        {
            handleException(ex, player, "Something went wrong while making the faction.");
        }
    }

    private void handleNowCommand(String[] args, Player player) //may be removed in the released version
    {
        if (args.length < 2)
        {
            player.sendMessage(ChatColor.RED + "/faction create [amount].");
            return;
        }

        try
        {
            for(int i = 0; i < Integer.parseInt(args[1]); i++){
                factionManager.create(UUID.randomUUID(), "#$#$#$#$#$#$#$#$GHGHGHGHGHGHGH#$#$#$#$#$$#$#" + i);
            }
        } catch (SQLException ex)
        {
            handleException(ex, player, "Something went wrong while making the faction.");
        }
    }

    private void handleDeleteCommand(Player player)
    {
        try
        {
            factionManager.delete(player);
        }
        catch (SQLException ex)
        {
            handleException(ex, player, "Something went wrong while deleting your faction.");
        }
    }

    private void handleListCommand(Player player)
    {
        try
        {
            factionManager.list(player);
        }
        catch (SQLException ex)
        {
            handleException(ex, player, "Somthing went wrong while getting every existing faction name.");
        }
    }

    private void handleMembersCommand(String[] args, Player player)
    {
        String factionName = null;
        if(args.length > 1) factionName = args[1];

        try
        {
            factionManager.members(player, factionName);
        }
        catch (SQLException ex)
        {
            handleException(ex, player, "Something went wrong while listing member of faction.");
        }
    }

    private void  handleOwnerCommand(String[] args, Player player)
    {
        String factionName = null;
        if(args.length > 1) factionName = args[1];

        try
        {
            factionManager.owner(player, factionName);
        }
        catch (SQLException ex)
        {
            handleException(ex, player, "Something went wrong while getting the owner of the faction.");
        }
    }

    private void handleLeaveCommand(Player player)
    {
        try
        {
            factionManager.leave(player);
        }
        catch (SQLException ex)
        {
            handleException(ex, player, "Something went wrong while getting the owner of the faction.");
        }
    }

    private void handleKickCommand(String[] args, Player player)
    {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/faction kick [Player name]");
            return;
        }

        try
        {
            factionManager.kick(player, args[1]);
        }
        catch (SQLException ex)
        {
            handleException(ex, player, "Something went wrong kicking a player from the faction.");

        }
    }

    private void handleInviteCommand(String[] args, Player player)
    {
        if(args.length < 2)
        {
            player.sendMessage(ChatColor.RED + "/faction invite [Player name]");
            return;
        }

        try
        {
            factionManager.invite(player, args[1]);
        }
        catch (SQLException ex)
        {
            handleException(ex, player, "Something went wrong while create the invite.");
        }
    }

    private void handleJoinCommand(String[] args, Player player)
    {
        if(args.length < 2){
            player.sendMessage(ChatColor.RED + "/faction join [Faction name]");
            return;
        }

        try
        {
            factionManager.join(player, args[1]);
        }
        catch (SQLException ex)
        {
            handleException(ex, player, "Something went wrong while making a join request.");
        }
    }

    private void handleAcceptCommand(Player player)
    {
        try
        {
            factionManager.accept(player);
        }
        catch (SQLException ex)
        {
            handleException(ex, player, "Something went wrong while accepting a invite or join request.");
        }
    }

    private void handleDeclineCommand(Player player)
    {
        try
        {
            factionManager.decline(player);
        }
        catch (SQLException ex)
        {
            handleException(ex, player, "Something went wrong while declining a invite or join request.");
        }
    }

    private void handleModifyCommand(String[] args, Player player){
        if(args.length < 2)
        {
            player.sendMessage("Available commands for factions modify are");
            player.sendMessage("/faction modify name [new faction name]");
            player.sendMessage("/faction modify color [color name]");
            player.sendMessage("/faction modify owner [member name]");
            return;
        }
        switch (args[1].toLowerCase())
        {
            case "name":
                handleModifyNameCommand(args, player);
                break;
            case "color":
                handleModifyColorCommand(args, player);
                break;
            case "owner":
                handleModifyOwnerCommand(args, player);
                break;
            default:
                player.sendMessage("Available commands for factions modify are");
                player.sendMessage("/faction modify name [new faction name]");
                player.sendMessage("/faction modify color [color name]");
                player.sendMessage("/faction modify owner [member name]");
                break;
        }
    }

    private void handleModifyNameCommand(String[] args, Player player)
    {
        if(args.length < 3)
        {
            player.sendMessage(ChatColor.RED + "/faction modify name [new Faction name]");
            return;
        }

        try
        {
            factionManager.modifyName(player, args[2]);
        }
        catch (SQLException ex)
        {
            handleException(ex, player, "Something went wrong while modifying the faction name.");
        }
    }

    private void handleModifyColorCommand(String[] args, Player player)
    {
        if(args.length < 3)
        {
            player.sendMessage(ChatColor.RED + "/faction modify name [Color]");
            return;
        }

        try
        {
            factionManager.modifyColor(player, args[2]);
        }
        catch (SQLException ex)
        {
            handleException(ex, player, "Something went wrong while modifying the faction color.");
        }
    }

    private void handleModifyOwnerCommand(String[] args, Player player)
    {
        if(args.length < 3)
        {
            player.sendMessage(ChatColor.RED + "/faction modify owner [member name]");
            return;
        }
        try
        {
            factionManager.modifyOwner(player, args[2]);
        } catch (SQLException ex)
        {
            handleException(ex, player, "Somthing went wrong while modifying the factions owner.");
        }
    }

    private void handleException(Exception ex, Player player, String message)
    {
        ex.printStackTrace();
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + message + ex.getMessage());
        player.sendMessage(ChatColor.RED + message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args)
    {
        if(!(sender instanceof Player))
        {
            return List.of();
        }

        List<String> suggestions = new ArrayList<>();

        switch (args.length)
        {
            case 1:
                suggestions.add("create");
                suggestions.add("delete");
                suggestions.add("list");
                suggestions.add("owner");
                suggestions.add("member");
                suggestions.add("leave");
                suggestions.add("kick");
                suggestions.add("invite");
                suggestions.add("join");
                suggestions.add("accept");
                suggestions.add("decline");
                suggestions.add("modify");
                break;
            case 2:
                if(args[0].toLowerCase().contains("modify"))
                {
                    suggestions.add("name");
                    suggestions.add("color");
                    suggestions.add("owner");
                }
                break;
        }

        return suggestions;
    }
}