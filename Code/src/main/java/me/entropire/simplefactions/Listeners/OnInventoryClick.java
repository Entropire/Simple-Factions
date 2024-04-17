package me.entropire.simplefactions.Listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.entropire.simplefactions.Gui;
import me.entropire.simplefactions.Simple_Factions;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class OnInventoryClick  implements Listener
{
    Gui gui;

    public OnInventoryClick(Simple_Factions simpleFactionsPlugin)
    {
        gui = new Gui(simpleFactionsPlugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        if(e.getInventory().getMaxStackSize() != 1) return;
        if(e.getCurrentItem() == null) return;

        e.setCancelled(true);

        String inventoryName = e.getView().getTitle();
        ItemStack clickedItem = e.getCurrentItem();
        Player player = (Player) e.getView().getPlayer();

        switch (inventoryName)
        {
            case "Simple-Factions":
                    HandleSimpleFactionGui(clickedItem, player);
                break;
        }

        if(inventoryName.contains("Factions page"))
        {
            HandleFactionListGui(clickedItem, player, inventoryName);
        }
    }

    private void HandleSimpleFactionGui(ItemStack clickedItem, Player player)
    {
        if(clickedItem.getItemMeta().getDisplayName().equals("Create"))
        {
            try
            {
                gui.FactionList(player, 0);
            } catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private void HandleFactionListGui(ItemStack clickedItem, Player player, String inventoryName)
    {
        if(clickedItem.getItemMeta().getDisplayName().equals("Leave"))
        {
            gui.SimpleFaction(player);
        }

        if(clickedItem.getType().equals(Material.STONE_BUTTON))
        {
            inventoryName = inventoryName.replace("Factions page ", "");
            int pageNumber = Integer.parseInt(inventoryName);

            if(clickedItem.getItemMeta().getDisplayName().equals("Previous"))
            {
                pageNumber--;
            }
            if(clickedItem.getItemMeta().getDisplayName().equals("Next"))
            {
                pageNumber++;
            }

            if(pageNumber < 0)
            {
                pageNumber = 0;
            }

            try
            {
                gui.FactionList(player, pageNumber);
            } catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private void HandleFactionJoinGui()
    {

    }
}
