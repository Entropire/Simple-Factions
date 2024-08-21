package me.entropire.simple_factions.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;

public class Anvil extends AnvilMenu
{
    public Anvil(Player player)
    {
        super(0, player.getInventory());
    }
}
