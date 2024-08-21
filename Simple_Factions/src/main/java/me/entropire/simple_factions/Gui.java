package me.entropire.simple_factions;

import me.entropire.simple_factions.objects.Colors;
import me.entropire.simple_factions.objects.Faction;
import me.entropire.simple_factions.objects.MenuHolder;
import me.entropire.simple_factions.objects.MenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class Gui
{
    private final Simple_Factions simpleFactionsPlugin;
    private final Colors colors = new Colors();

    public Gui(Simple_Factions simpleFactionsPlugin)
    {
        this.simpleFactionsPlugin = simpleFactionsPlugin;
    }

    public void SimpleFaction(Player player)
    {
        Inventory inventory = Bukkit.createInventory(new MenuHolder(MenuTypes.SimpleFaction), 27, "Simple-Factions");
        inventory.setMaxStackSize(1);

        inventory.setItem(11, CreateItem("Create new faction", Material.ANVIL, "Create a new faction."));
        inventory.setItem(15, CreateItem("Join faction", Material.NAME_TAG, "Join a faction."));

        player.openInventory(inventory);
    }

    public void FactionList(Player player, int pageNumber) throws SQLException
    {
        Inventory inventory = Bukkit.createInventory(new MenuHolder(MenuTypes.FactionList), 54, "Factions page " + pageNumber);
        ArrayList<String> factions = simpleFactionsPlugin.factionDatabase.getFactions();

        if(!factions.isEmpty())
        {
            int index = 0;
            for(int i = 45 * pageNumber, j = Math.min(45 * (pageNumber + 1), factions.size()); i < j; i++)
            {
                inventory.setItem(index, CreateItem(factions.get(i), Material.PLAYER_HEAD, ""));
                index++;
            }
        }

        float pageAmount = (float) factions.size() / 45;
        ItemStack fillItem = CreateItem("", Material.GRAY_STAINED_GLASS_PANE, "");

        if(pageNumber < pageAmount - 1)
        {
            inventory.setItem(53, CreateItem("Next", Material.STONE_BUTTON, "Go to the next page."));
        }
        else
        {
            inventory.setItem(53, fillItem);
        }

        if(pageNumber > 0)
        {
            inventory.setItem(45, CreateItem("Previous", Material.STONE_BUTTON, "Go to the previous page."));
        }
        else
        {
            inventory.setItem(45, fillItem);
        }

        inventory.setItem(49, CreateItem("Leave", Material.RED_WOOL, "Go back to the main menu."));
        inventory.setItem(46, fillItem);
        inventory.setItem(47, fillItem);
        inventory.setItem(48, fillItem);
        inventory.setItem(50, fillItem);
        inventory.setItem(51, fillItem);
        inventory.setItem(52, fillItem);

        player.openInventory(inventory);
    }

    public void FactionInfo(Player player, String factionName) throws SQLException
    {
        Inventory inventory = Bukkit.createInventory(new MenuHolder(MenuTypes.FactionInfo), 27, "Info of " + factionName);

        Faction faction = simpleFactionsPlugin.factionDatabase.getFactionDataByName(factionName);

        if(faction == null) { player.sendMessage(ChatColor.RED + "Somthing whent rong while getting the factions information."); return; }

        Player owner =  Bukkit.getPlayer(faction.owner());
        String ownerName;
        if(owner == null)
        {
            ownerName = "";
        }
        else
        {
            ownerName = owner.getName();
        }
        ArrayList<String> members = faction.members();
        ArrayList<String> top10Members = faction.members();

        for(int i = 0; i < 10; i++)
        {
            top10Members.add(members.get(i));
        }

        inventory.setItem(2, CreateItem("Faction name", Material.NAME_TAG, factionName));
        inventory.setItem(4, CreateItem("Faction owner", Material.PLAYER_HEAD, ownerName));
        inventory.setItem(6, CreateItem("Faction members", Material.OAK_SIGN, top10Members));
        inventory.setItem(21, CreateItem("Join", Material.GREEN_WOOL, "Request to join this faction."));
        inventory.setItem(23, CreateItem("Leave", Material.RED_WOOL, "Go back to the previous page."));

        player.openInventory(inventory);
    }

    public void CreateFaction(Player player)
    {
        Inventory inventory = Bukkit.createInventory(new MenuHolder(MenuTypes.CreateFaction), 27, "New faction");

        String factionName;
        ChatColor factionColor;

        if(simpleFactionsPlugin.createFactions.containsKey(player.getUniqueId()))
        {
            Faction faction = simpleFactionsPlugin.createFactions.get(player.getUniqueId());
            factionName = faction.name();
            factionColor = faction.color();
        }
        else
        {
            ArrayList<String> members = new ArrayList<>();
            members.add(player.getName());

            Faction faction = new Faction(0, "New Faction",  ChatColor.WHITE, player.getUniqueId(), members);
            simpleFactionsPlugin.createFactions.put(player.getUniqueId(), faction);

            factionName = faction.name();
            factionColor = faction.color();
        }

        inventory.setItem(2, CreateItem("Faction name", Material.NAME_TAG, factionName));
        inventory.setItem(6, CreateItem("Faction color", colors.getMaterialWithChatColor(factionColor), factionColor + colors.getColorNameWithChatColor(factionColor)));
        inventory.setItem(23, CreateItem("Discard", Material.RED_WOOL, "Discard your faction creation."));

        if(!factionName.contains("New Faction"))
        {
            inventory.setItem(21, CreateItem("Create", Material.GREEN_WOOL, "Create your new faction."));
        }

        player.openInventory(inventory);
    }

    public void ChangeFactionName(Player player)
    {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        BlockPos position = serverPlayer.blockPosition();

        AnvilMenu anvilMenu = new AnvilMenu(0, serverPlayer.getInventory(), ContainerLevelAccess.create(serverPlayer.level(), position));

        anvilMenu.setTitle(Component.translatable("Hello World"));

        CraftEventFactory.callInventoryOpenEvent(serverPlayer, anvilMenu);

        player.sendMessage("Opened anvil menu");
    }

    private ItemStack CreateItem(String name, Material material, String lore)
    {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Collections.singletonList(lore));
        itemStack.setItemMeta(itemMeta);
        return  itemStack;
    }

    private ItemStack CreateItem(String name, Material material, ArrayList<String> lore)
    {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return  itemStack;
    }
}