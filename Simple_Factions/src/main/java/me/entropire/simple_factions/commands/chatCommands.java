package me.entropire.simple_factions.commands;

import me.entropire.simple_factions.Simple_Factions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public class chatCommands implements CommandExecutor, TabCompleter
{

    private final Simple_Factions simpleFactionsPlugin;

    public chatCommands(Simple_Factions simpleFactionsPlugin)
    {
        this.simpleFactionsPlugin = simpleFactionsPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player player))
        {
            sender.sendMessage(RED + "Only players can use this command.");
            return false;
        }

        ArrayList<String> chats = new ArrayList<>(Arrays.asList("public", "faction"));
        if(args.length < 1 || !chats.contains(args[0].toLowerCase()))
        {
            player.sendMessage("command usage: /chat [public or faction]");
            return false;
        }
        try {
            if(!simpleFactionsPlugin.playerDatabase.hasFaction(player))
            {
                player.sendMessage(RED + "You must be in a faction to change chat.");
                return false;
            }
            simpleFactionsPlugin.playerDatabase.setChat(player.getUniqueId(), args[0].toLowerCase());
            player.sendMessage(GREEN + "Set chat to " + args[0]);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            Bukkit.getServer().getConsoleSender().sendMessage(RED + "Somthing went rong while change chat" + ex.getMessage());
            player.sendMessage(RED + "Somthing went rong while change chat");
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args)
    {
        List<String> suggestions = new ArrayList<>();

        if(sender instanceof Player)
        {
            if(args.length == 1)
            {
                suggestions.add("Public");
                suggestions.add("Faction");
            }

            return  suggestions;
        }

        return List.of();
    }
}
