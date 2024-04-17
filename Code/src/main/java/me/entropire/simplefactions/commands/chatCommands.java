package me.entropire.simplefactions.commands;

import me.entropire.simplefactions.Simple_Factions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.bukkit.ChatColor.*;

public class chatCommands implements CommandExecutor {

    private final Simple_Factions simpleFactionsPlugin;

    public chatCommands(Simple_Factions simpleFactionsPlugin)
    {
        this.simpleFactionsPlugin = simpleFactionsPlugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
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
            simpleFactionsPlugin.playerDatabase.setChat(player.getUniqueId(), args[0]);
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
}
