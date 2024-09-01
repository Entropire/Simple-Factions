package me.entropire.simple_factions.objects;

import org.bukkit.event.inventory.InventoryClickEvent;

@FunctionalInterface
public interface ButtonPressAction {
    void onPress(Button button, InventoryClickEvent event);
}