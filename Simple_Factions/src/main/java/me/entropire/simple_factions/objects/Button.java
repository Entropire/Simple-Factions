package me.entropire.simple_factions.objects;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class Button extends ItemStack
{
    private ButtonPressAction action;

    public Button(Material material, ButtonPressAction action)
    {
        super(material);
        this.action = action;
    }

    public void onPressed(InventoryClickEvent event)
    {
        if (action != null)
        {
            action.onPress(this, event);
        }
    }

    public ButtonPressAction getAction()
    {
        return action;
    }

    public void setAction(ButtonPressAction action)
    {
        this.action = action;
    }
}
