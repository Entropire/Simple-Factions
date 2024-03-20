package me.entropire.simplefactions.objects;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.UUID;

public record Faction(int id, String name, ChatColor color, UUID owner, ArrayList<String> members)
{
    // Constructor
}