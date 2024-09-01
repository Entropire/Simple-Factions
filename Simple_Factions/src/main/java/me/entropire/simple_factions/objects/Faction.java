package me.entropire.simple_factions.objects;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.UUID;

public class Faction {
    private int id;
    private String name;
    private ChatColor color;
    private UUID owner;
    private ArrayList<String> members;

    public Faction(int id, String name, ChatColor color, UUID owner, ArrayList<String> members) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.owner = owner;
        this.members = members;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public UUID getOwner() {
        return owner;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setName(String name) {
        this.name = name;
    }

    public  void setColor(ChatColor color)
    {
        this.color = color;
    }
}