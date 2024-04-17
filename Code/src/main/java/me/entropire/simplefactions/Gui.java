package me.entropire.simplefactions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class Gui
{
    private final Simple_Factions simpleFactionsPlugin;

    public Gui(Simple_Factions simpleFactionsPlugin)
    {
        this.simpleFactionsPlugin = simpleFactionsPlugin;
    }

    public void SimpleFaction(Player player)
    {
        Inventory inventory = Bukkit.createInventory(null, 27, "Simple-Factions");
        inventory.setMaxStackSize(1);

        inventory.setItem(11, CreateItem("Create", Material.ANVIL, "Create a new faction."));
        inventory.setItem(15, CreateItem("Join faction", Material.NAME_TAG, "Join a faction."));

        player.openInventory(inventory);
    }

    public void FactionList(Player player, int pageNumber) throws SQLException
    {
        Inventory inventory = Bukkit.createInventory(null, 54, "Factions page " + pageNumber);
        inventory.setMaxStackSize(1);
        ArrayList<String> factions = simpleFactionsPlugin.factionDatabase.getFactions();

        if(!factions.isEmpty())
        {
            int max = Math.min(45 * (pageNumber + 1), factions.size());

            int index = 0;
            for(int i = 45 * pageNumber, j = i + max; i <= j; i++)
            {
                inventory.setItem(index, CreateItem(factions.get(i), Material.PLAYER_HEAD, ""));
                index++;
            }
        }

        player.openInventory(inventory);
    }

    private ItemStack CreateItem(String name, Material material, String lore)
    {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Collections.singletonList(lore));
        itemStack.setItemMeta(itemMeta);
        return  itemStack;
    }
}
