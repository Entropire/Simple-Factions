package me.entropire.simplefactions.Listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.entropire.simplefactions.Gui;
import me.entropire.simplefactions.Simple_Factions;
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
}
