package me.entropire.simplefactions.Listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.entropire.simplefactions.FactionManager;
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
    FactionManager factionManager;
    Gui gui;

    public OnInventoryClick(Simple_Factions simpleFactionsPlugin)
    {
        factionManager = new FactionManager(simpleFactionsPlugin);
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

        if(inventoryName.contains("Info of "))
        {
            HandleFactionJoinGui(clickedItem, player, inventoryName);
        }
    }

    private void HandleSimpleFactionGui(ItemStack clickedItem, Player player)
    {
        if(clickedItem.getItemMeta().getDisplayName().equals("Join faction") && clickedItem.getType().equals(Material.NAME_TAG))
        {
            try
            {
                gui.FactionList(player, 0);
            } catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }

        if(clickedItem.getItemMeta().getDisplayName().equals("Create new faction") && clickedItem.getType().equals(Material.ANVIL))
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
        if(clickedItem.getType().equals(Material.PLAYER_HEAD))
        {
            try
            {
                gui.FactionInfo(player, clickedItem.getItemMeta().getDisplayName());
            } catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }

        if(clickedItem.getItemMeta().getDisplayName().equals("Leave") && clickedItem.getType().equals(Material.RED_WOOL))
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

    private void HandleFactionJoinGui(ItemStack clickedItem, Player player, String inventoryName)
    {
        if(clickedItem.getItemMeta().getDisplayName().equals("Join") && clickedItem.getType().equals(Material.GREEN_WOOL))
        {
            player.closeInventory();

            inventoryName = inventoryName.replace("Info of ", "");

            try
            {
                factionManager.join(player,inventoryName);
            } catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }

        if(clickedItem.getItemMeta().getDisplayName().equals("Leave") && clickedItem.getType().equals(Material.RED_WOOL))
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
}
