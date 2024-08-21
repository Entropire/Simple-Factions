package me.entropire.simple_factions.objects;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;

public class Colors
{
    public final ArrayList<String> colorName = new ArrayList<>(Arrays.asList("black", "red", "aqua", "blue", "dark_aqua", "dark_blue", "dark_gray", "dark_green", "dark_purple", "dark_red", "gold", "gray", "green", "light_purple", "white", "yellow"));
    public final ArrayList<ChatColor> chatColor = new ArrayList<>(Arrays.asList(ChatColor.BLACK, ChatColor.RED, ChatColor.AQUA, ChatColor.BLUE, ChatColor.DARK_AQUA, ChatColor.DARK_BLUE, ChatColor.DARK_GRAY, ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE, ChatColor.DARK_RED, ChatColor.GOLD, ChatColor.GRAY, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.WHITE, ChatColor.YELLOW));
    public final ArrayList<Material> material = new ArrayList<>(Arrays.asList(Material.BLACK_WOOL, Material.RED_WOOL, Material.LIGHT_BLUE_WOOL, Material.BLUE_WOOL, Material.CYAN_WOOL, Material.BLUE_WOOL, Material.GRAY_WOOL, Material.GREEN_WOOL, Material.PURPLE_WOOL, Material.RED_WOOL, Material.ORANGE_WOOL, Material.LIGHT_GRAY_WOOL, Material.LIME_WOOL, Material.MAGENTA_WOOL, Material.WHITE_WOOL, Material.YELLOW_WOOL));

    public String getColorNameWithChatColor(ChatColor chatColor)
    {
        int i = this.chatColor.indexOf(chatColor);
        return i != -1 ? (String)this.colorName.get(i) : null;
    }

    public String getColorNameWithMaterial(Material material)
    {
        int i = this.material.indexOf(material);
        return i != -1 ? (String)this.colorName.get(i) : null;
    }

    public ChatColor getChatColorWithColorName(String colorName)
    {
        int i = this.colorName.indexOf(colorName.toLowerCase());
        return i != -1 ? (ChatColor)this.chatColor.get(i) : null;
    }

    public ChatColor getChatColorWithMaterial(Material material)
    {
        int i = this.material.indexOf(material);
        return i != -1 ? (ChatColor)this.chatColor.get(i) : null;
    }

    public Material getMaterialWithColorName(String colorName)
    {
        int i = this.colorName.indexOf(colorName);
        return i != -1 ? (Material)this.material.get(i) : null;
    }

    public Material getMaterialWithChatColor(ChatColor chatColor)
    {
        int i = this.chatColor.indexOf(chatColor);
        return i != -1 ? (Material)this.material.get(i) : null;
    }
}