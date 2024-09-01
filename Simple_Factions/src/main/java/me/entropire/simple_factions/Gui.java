package me.entropire.simple_factions;

import me.entropire.simple_factions.objects.Colors;
import me.entropire.simple_factions.objects.Faction;
import me.entropire.simple_factions.objects.MenuHolder;
import me.entropire.simple_factions.objects.MenuTypes;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Gui
{
    private final Simple_Factions simpleFactionsPlugin;
    private final Colors colors = new Colors();
    private final FactionManager factionManager;

    public Gui(Simple_Factions simpleFactionsPlugin)
    {
        this.factionManager = new FactionManager(simpleFactionsPlugin);
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

        Player owner =  Bukkit.getPlayer(faction.getOwner());
        String ownerName;
        if(owner == null)
        {
            ownerName = "";
        }
        else
        {
            ownerName = owner.getName();
        }
        ArrayList<String> members = faction.getMembers();
        ArrayList<String> top10Members = faction.getMembers();

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
            factionName = faction.getName();
            factionColor = faction.getColor();
        }
        else
        {
            ArrayList<String> members = new ArrayList<>();
            members.add(player.getName());

            Faction faction = new Faction(0, "New Faction",  ChatColor.WHITE, player.getUniqueId(), members);
            simpleFactionsPlugin.createFactions.put(player.getUniqueId(), faction);

            factionName = faction.getName();
            factionColor = faction.getColor();
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

    public void SetFactionName(Player player)
    {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }

                    if(!stateSnapshot.getText().equalsIgnoreCase("name"))
                    {
                        simpleFactionsPlugin.createFactions.get(player.getUniqueId()).setName(stateSnapshot.getText());
                        CreateFaction(player);
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }
                    else
                    {
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("name"));
                    }
                })
                .text("name")
                .title("Set faction name")
                .plugin(simpleFactionsPlugin)
                .open(player);
    }

    public void SetFactionColor(Player player)
    {
        Inventory inventory = Bukkit.createInventory(new MenuHolder(MenuTypes.SetFactionColor), 27, "Set faction color");

        int i = 0;
        List<Integer> slots = Arrays.asList(1,2,3,4,5,6,7,10,11,12,13,14,15,16,21,23);
        for (String colorName : colors.colorName)
        {
            inventory.setItem(slots.get(i), CreateItem(colors.getChatColorWithColorName(colorName) + colorName, colors.getMaterialWithColorName(colorName), ""));
            i++;
        }

        player.openInventory(inventory);
    }

    public void Faction(Player player, Faction faction) throws SQLException {
        Inventory inventory = Bukkit.createInventory(new MenuHolder(MenuTypes.Faction), 27, faction.getName());

        inventory.setItem(10, CreateItem("Faction name", Material.NAME_TAG, faction.getName()));
        inventory.setItem(12, CreateItem("Faction color", colors.getMaterialWithChatColor(faction.getColor()), faction.getColor() + colors.getColorNameWithChatColor(faction.getColor())));
        inventory.setItem(14, CreateItem("Owner", Material.PLAYER_HEAD, simpleFactionsPlugin.playerDatabase.getPlayerName(faction.getOwner().toString())));
        ArrayList<String> members = new ArrayList<>();
        for (int i = 0; i < Math.min(9, faction.getMembers().size()); i++) {
            members.add(faction.getMembers().get(i));
        }
        inventory.setItem(16, CreateItem("Members", Material.OAK_SIGN, members));

        if(faction.getOwner().equals(player.getUniqueId()))
        {
            inventory.setItem(18, CreateItem("Invite Player", Material.PAPER,  ""));
            inventory.setItem(26, CreateItem("Delete Faction", Material.RED_WOOL,  ""));
        }
        else
        {
            inventory.setItem(26, CreateItem("Leave Faction", Material.RED_WOOL,  ""));
        }

        player.openInventory(inventory);
    }

    public void ChangeFactionName(Player player)
    {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    Faction faction;
                    try {
                        int factionId = simpleFactionsPlugin.playerDatabase.getFactionId(player);
                        faction  = simpleFactionsPlugin.factionDatabase.getFactionDataById(factionId);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    if(!stateSnapshot.getText().equalsIgnoreCase(faction.getName()))
                    {
                        try {
                            factionManager.modifyName(player, faction.getName());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    }
                    else
                    {
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("name"));
                    }
                })
                .text("name")
                .title("Change faction name")
                .plugin(simpleFactionsPlugin)
                .open(player);
    }

    public void ChangeFactionColor(Player player)
    {
        Inventory inventory = Bukkit.createInventory(new MenuHolder(MenuTypes.ChangeFactionColor), 27, "Change faction color");

        int i = 0;
        List<Integer> slots = Arrays.asList(1,2,3,4,5,6,7,10,11,12,13,14,15,16,21,23);
        for (String colorName : colors.colorName)
        {
            inventory.setItem(slots.get(i), CreateItem(colors.getChatColorWithColorName(colorName) + colorName, colors.getMaterialWithColorName(colorName), ""));
            i++;
        }

        player.openInventory(inventory);
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