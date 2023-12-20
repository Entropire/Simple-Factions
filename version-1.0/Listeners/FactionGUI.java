package me.entropire.simplefactions.Listeners;

import me.entropire.simplefactions.Faction;
import me.entropire.simplefactions.FactionManager;
import me.entropire.simplefactions.GUI;
import me.entropire.simplefactions.InventoryList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static me.entropire.simplefactions.Simple_Factions.*;
import static org.bukkit.ChatColor.*;

public class FactionGUI implements Listener {
    GUI gui = new GUI();
    FactionManager factionManager = new FactionManager();

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e)
    {
        Player player = (Player) e.getPlayer();
        if(e.getView().getTitle().contains("Faction list"))
        {
            factionListInventories.remove(player.getUniqueId());
            return;
        }

        if(e.getView().getTitle().contains("Member list"))
        {
            memberListInventories.remove(player.getUniqueId());
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        Inventory inventory = e.getInventory();
        String inventoryName = e.getView().getTitle();

        if(item == null) return;
        if(inventoryName.toLowerCase().contains("faction") || inventoryName.toLowerCase().contains("factions") || inventoryName.toLowerCase().contains("member"))
        {
            e.setCancelled(true);

            if(inventoryName.equals("Simple-Factions"))
            {
                if(item.getType().equals(Material.ANVIL))
                {
                    inventory.close();
                    gui.createFaction(player);
                    return;
                }

                if(item.getType().equals(Material.NAME_TAG))
                {
                    inventory.close();
                    gui.joinFactionList(player);
                    return;
                }
            }

            if(inventoryName.contains("Faction list"))
            {
                if(item.getType().equals(Material.PLAYER_HEAD))
                {
                    String joinFactionName = item.getItemMeta().getDisplayName();
                    Faction joinFaction = factions.get(joinFactionName);
                    inventory.close();
                    gui.factionJoinRequest(player, joinFaction);
                    return;
                }

                if(item.getItemMeta().getDisplayName().equals("Next"))
                {
                    InventoryList factionsInvData = factionListInventories.get(player.getUniqueId());
                    factionsInvData.setInt(factionsInvData.getInt() + 1);
                    player.openInventory(factionsInvData.getInventories().get(factionsInvData.getInt()));
                    return;
                }

                if(item.getItemMeta().getDisplayName().equals("Previous"))
                {
                    InventoryList factionsInvData = factionListInventories.get(player.getUniqueId());
                    factionsInvData.setInt(factionsInvData.getInt() - 1);
                    player.openInventory(factionsInvData.getInventories().get(factionsInvData.getInt()));
                    return;
                }
            }

            if(inventoryName.contains("Join faction"))
            {
                if(item.getType().equals(Material.GREEN_WOOL))
                {
                    String joinFactionName = item.getItemMeta().getDisplayName();
                    joinFactionName = joinFactionName.replace("Join faction ", "");
                    inventory.close();
                    factionManager.joinFaction(player, joinFactionName);
                    return;
                }

                if(item.getType().equals(Material.RED_WOOL))
                {
                    inventory.close();
                    gui.joinFactionList(player);
                    return;
                }
            }

            if(inventoryName.equals("Create faction"))
            {
                if(item.getItemMeta().getDisplayName().contains("Create"))
                {
                    e.getInventory().close();
                    Faction createFaction = createFactions.get(player.getUniqueId());
                    factionManager.createFaction(player, createFaction.factionName, createFaction.color);
                    createFactions.remove(player.getUniqueId());
                    return;
                }

                if(item.getType().equals(Material.NAME_TAG))
                {
                    player.sendMessage(YELLOW + "Type your faction name:");
                    namingFactions.put(player.getUniqueId(), "waiting");
                    e.getInventory().close();
                    return;
                }

                if(item.getType().toString().contains("WOOL"))
                {
                    inventory.close();
                    gui.setFactionColor(player);
                    return;
                }

                if(item.getType().equals(Material.LAVA_BUCKET))
                {
                    createFactions.remove(player.getUniqueId());
                    player.sendMessage(RED + "You have discarded your faction creation");
                    e.getInventory().close();
                    return;
                }
            }

            if(inventoryName.equals("Set faction color"))
            {
                boolean uses;
                uses = createFactions.containsKey(player.getUniqueId());
                e.getInventory().close();
                factionManager.factionSetColor(player, item.getItemMeta().getDisplayName(), uses);

                if(uses) gui.createFaction(player);
                else gui.faction(player);
                return;
            }

            Faction faction = factions.get(players.get(player.getUniqueId()));
            if(faction == null) return;

            if(inventoryName.equals("Faction " + faction.factionName))
            {
                if(faction.owner.equals(player.getUniqueId())){
                    if(item.getType().equals(Material.NAME_TAG)){
                        player.sendMessage(YELLOW + "Type your faction name:");
                        namingFactions.put(player.getUniqueId(), "waiting");
                        inventory.close();
                        return;
                    }

                    if(item.getItemMeta().getDisplayName().equals("Faction color"))
                    {
                        inventory.close();
                        gui.setFactionColor(player);
                        return;
                    }

                    if (item.getType().equals(Material.OAK_SIGN) || item.getType().equals(Material.PLAYER_HEAD))
                    {
                        e.getInventory().close();
                        gui.memberList(player);
                        return;
                    }

                    if(item.getType().equals(Material.LAVA_BUCKET)){
                        inventory.close();
                        gui.deleteFaction(player);
                        return;
                    }
                }

                if(item.getType().equals(Material.LAVA_BUCKET)){
                    inventory.close();
                    gui.leaveFaction(player);
                    return;
                }
            }

            if(inventoryName.contains("Member list"))
            {
                InventoryList membersInvData = memberListInventories.get(player.getUniqueId());

                if(item.getType().equals(Material.PLAYER_HEAD))
                {
                    String factionMember = item.getItemMeta().getDisplayName();
                    Player member = Bukkit.getPlayer(factionMember);
                    if(member == null) return;
                    inventory.close();
                    gui.memberInfo(player, member);
                    return;
                }

                if(item.getItemMeta().getDisplayName().equals("Next"))
                {
                    membersInvData.setInt(membersInvData.getInt() + 1);
                    player.openInventory(membersInvData.getInventories().get(membersInvData.getInt()));
                    return;
                }

                if(item.getItemMeta().getDisplayName().equals("Previous"))
                {
                    membersInvData.setInt(membersInvData.getInt() - 1);
                    player.openInventory(membersInvData.getInventories().get(membersInvData.getInt()));
                    return;
                }
            }

            if(inventoryName.contains("Faction member"))
            {
                String memberName = inventoryName.replace("Faction member ", "");

                if(item.getType().equals(Material.LAVA_BUCKET)){
                    factionManager.kickMember(player, memberName);
                    inventory.close();
                    gui.faction(player);
                    return;
                }

                if(item.getType().equals(Material.EGG)){
                    factionManager.factionSetOwner(player, memberName);
                    inventory.close();
                    gui.faction(player);
                    return;
                }
            }

            if(inventoryName.contains("Delete faction"))
            {
                if(item.getType().equals(Material.GREEN_WOOL))
                {
                    e.getInventory().close();
                    factionManager.deleteFaction(player, faction.factionName);
                    return;
                }

                if(item.getType().equals(Material.RED_WOOL))
                {
                    e.getInventory().close();
                    gui.faction(player);
                    return;
                }
            }

            if(inventoryName.contains("Leave faction"))
            {
                if(item.getType() == Material.GREEN_WOOL)
                {
                    e.getInventory().close();
                    factionManager.leaveFaction(player);
                    return;
                }

                if(item.getType() == Material.RED_WOOL)
                {
                    e.getInventory().close();
                    gui.faction(player);
                }
            }
        }
    }
}
