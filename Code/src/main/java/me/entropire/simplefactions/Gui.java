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
            int index = 0;
            for(int i = 45 * pageNumber, j = Math.min(45 * (pageNumber + 1), factions.size()); i < j; i++)
            {
                inventory.setItem(index, CreateItem(factions.get(i), Material.PLAYER_HEAD, ""));
                index++;
            }
        }

        float pageAmount = (float) factions.size() / 45;
        ItemStack fillItem = CreateItem("", Material.GRAY_STAINED_GLASS_PANE, "");

        if(pageNumber < pageAmount - 1)
        {
            inventory.setItem(53, CreateItem("Next", Material.STONE_BUTTON, "Go to the next page."));
        }
        else
        {
            inventory.setItem(53, fillItem);
        }

        if(pageNumber > 0)
        {
            inventory.setItem(45, CreateItem("Previous", Material.STONE_BUTTON, "Go to the previous page."));
        }
        else
        {
            inventory.setItem(45, fillItem);
        }

        inventory.setItem(49, CreateItem("Leave", Material.RED_WOOL, "Go back to the main menu."));
        inventory.setItem(46, fillItem);
        inventory.setItem(47, fillItem);
        inventory.setItem(48, fillItem);
        inventory.setItem(50, fillItem);
        inventory.setItem(51, fillItem);
        inventory.setItem(52, fillItem);

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
