package me.entropire.simplefactions.Libraries;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;

import static org.bukkit.ChatColor.*;
import static org.bukkit.Material.*;

public class Colors {
    ArrayList<String> colorName = new ArrayList<>(Arrays.asList("black", "red", "aqua", "blue", "dark_aqua", "dark_blue", "dark_gray", "dark_green", "dark_purple", "dark_red", "gold", "gray", "green", "light_purple", "white", "yellow"));
    ArrayList<ChatColor> chatColor = new ArrayList<>(Arrays.asList(BLACK, RED, AQUA, BLUE, DARK_AQUA, DARK_BLUE, DARK_GRAY, DARK_GREEN, DARK_PURPLE, DARK_RED, GOLD, GRAY, GREEN, LIGHT_PURPLE, WHITE, YELLOW));
    ArrayList<Material> material = new ArrayList<>(Arrays.asList(BLACK_WOOL, RED_WOOL,Material.LIGHT_BLUE_WOOL, BLUE_WOOL, CYAN_WOOL, BLUE_WOOL, GRAY_WOOL, GREEN_WOOL, PURPLE_WOOL, RED_WOOL, ORANGE_WOOL, LIGHT_GRAY_WOOL, LIME_WOOL, MAGENTA_WOOL, WHITE_WOOL, YELLOW_WOOL));

    public String getColorNameWithChatColor(ChatColor chatColor){
        int i = this.chatColor.indexOf(chatColor);

        if (i != -1) {
            return this.colorName.get(i);
        } else {
            return null;
        }
    }

    public String getColorNameWithMaterial(Material material){
        int i = this.material.indexOf(material);

        if (i != -1) {
            return this.colorName.get(i);
        } else {
            return null;
        }
    }

    public ChatColor getChatColorWithColorName(String colorName){
        int i = this.colorName.indexOf(colorName);
;
        if (i != -1)
        {
            return this.chatColor.get(i);
        }
        else
        {
            return null;
        }
    }

    public ChatColor getChatColorWithMaterial(Material material){
        int i = this.material.indexOf(material);

        if (i != -1)
        {
            return this.chatColor.get(i);
        }
        else
        {
            return null;
        }
    }

    public Material getMaterialWithColorName(String colorName){
        int i = this.colorName.indexOf(colorName);

        if (i != -1)
        {
            return this.material.get(i);
        }
        else
        {
            return null;
        }
    }

    public Material getMaterialWithChatColor(ChatColor chatColor){
        int i = this.chatColor.indexOf(chatColor);

        if (i != -1)
        {
            return this.material.get(i);
        }
        else
        {
            return null;
        }
    }
}