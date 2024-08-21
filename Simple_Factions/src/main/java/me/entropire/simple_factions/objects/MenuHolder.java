package me.entropire.simple_factions.objects;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MenuHolder implements InventoryHolder
{
    public MenuTypes menuType;

    public MenuHolder (MenuTypes menuType)
    {
        this.menuType = menuType;
    }

    public Inventory getInventory()
    {
        return  null;
    }
}
