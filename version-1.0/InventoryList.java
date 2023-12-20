package me.entropire.simplefactions;

import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class InventoryList {
    ArrayList<Inventory> inventories;
    int i = 0;

    public InventoryList(ArrayList<Inventory> inventoryList){
        this.inventories = inventoryList;
    }

    public int getInt(){
        return this.i;
    }

    public void setInt(int integer){
        this.i = integer;
    }

    public ArrayList<Inventory> getInventories(){
        return this.inventories;
    }
}
