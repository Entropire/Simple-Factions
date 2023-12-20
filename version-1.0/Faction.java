package me.entropire.simplefactions;

import me.entropire.simplefactions.Libraries.Colors;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Faction {
    Colors colors = new Colors();
    public ChatColor color = ChatColor.GRAY;
    public UUID owner;
    public String factionName;
    public Map<UUID, String> members = new HashMap<>();

    public Faction(String factionName, UUID player) {
        this.factionName = factionName;
        this.owner = player;
        this.members.put(player, "Owner");
    }

    public void addMember(UUID player){
        this.members.put(player, "member");
    }

    public void removeMember(UUID player){ this.members.remove(player);}

    public void consoleSetColor(String color){
        this.color = colors.getChatColorWithColorName(color);
    }

    public void consoleSetMembers(Map<UUID, String> members) {
        this.members = members;
    }

    public void setColor(ChatColor color){
        this.color = color;
    }

    public void setFactionName(String factionName) {
        this.factionName = factionName;
    }

    public void setOwner(Player sender, UUID player){
        this.members.replace(sender.getUniqueId(), "Member");
        this.members.replace(player, "Owner");
        this.owner = player;
    }
}

