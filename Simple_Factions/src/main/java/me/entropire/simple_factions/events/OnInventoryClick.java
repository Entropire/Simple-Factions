package me.entropire.simple_factions.events;

import me.entropire.simple_factions.objects.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class OnInventoryClick  implements Listener
{
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        if(!(e.getInventory().getHolder() instanceof Gui gui)) return;

        e.setCancelled(true);

        int slot = e.getSlot();

        Button button = gui.getButton(slot);
        button.onPressed(e);
    }
}