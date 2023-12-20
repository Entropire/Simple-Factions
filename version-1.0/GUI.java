package me.entropire.simplefactions;

import me.entropire.simplefactions.Libraries.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static me.entropire.simplefactions.Simple_Factions.*;

public class GUI {
    Colors colors = new Colors();
    public void noFaction(Player player)
    {
        Inventory inventory = Bukkit.createInventory(player, 9, "Simple-Factions");

        ItemStack createFaction = new ItemStack(Material.ANVIL);
        ItemMeta createFactionMeta = createFaction.getItemMeta();
        createFactionMeta.setDisplayName("Create faction");
        createFaction.setItemMeta(createFactionMeta);

        ItemStack joinFaction = new ItemStack(Material.NAME_TAG);
        ItemMeta joinFactionMeta = joinFaction.getItemMeta();
        joinFactionMeta.setDisplayName("Join faction");
        joinFaction.setItemMeta(joinFactionMeta);

        inventory.setItem(3, createFaction);
        inventory.setItem(5, joinFaction);

        player.openInventory(inventory);
    }

    public void joinFactionList(Player player)
    {
        ArrayList<Inventory> factionListInventory = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        for(Map.Entry<String, Faction> entry : factions.entrySet()){
            names.add(entry.getKey());
        }

        double amount = Simple_Factions.factions.size();
        double inventoryAmount = amount / 45;
        inventoryAmount = Math.ceil(inventoryAmount);
        int index = 0;

        for(int i = 0; i < inventoryAmount; i++){
            Inventory inventory = Bukkit.createInventory(player, 54, "Faction list " + i);
            int timesToLoop = Math.min(factions.size() - (i * 45), 45);

            for (int j = 0; j < timesToLoop ; j++){
                ItemStack faction = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta factionMeta = faction.getItemMeta();
                factionMeta.setDisplayName(names.get(index));
                faction.setItemMeta(factionMeta);

                inventory.addItem(faction);
                index++;
            }

            ItemStack nextPage = new ItemStack(Material.STONE_BUTTON);
            ItemMeta nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName("Next");
            nextPage.setItemMeta(nextPageMeta);

            ItemStack previousPage = new ItemStack(Material.STONE_BUTTON);
            ItemMeta previousPageMeta = previousPage.getItemMeta();
            previousPageMeta.setDisplayName("Previous");
            previousPage.setItemMeta(previousPageMeta);

            if(i == 0){
                if(amount > 45){
                    inventory.setItem(53, nextPage);
                }
            } else if (i == inventoryAmount) {
                inventory.setItem(46, previousPage);
            }else {
                inventory.setItem(53, nextPage);
                inventory.setItem(46, previousPage);
            }

            factionListInventory.add(inventory);
        }
        InventoryList inventoryList = new InventoryList(factionListInventory);
        Simple_Factions.memberListInventories.put(player.getUniqueId(), inventoryList);

        player.openInventory(factionListInventory.get(0));
    }

    public  void factionJoinRequest(Player player, Faction faction)
    {
        Inventory inventory = Bukkit.createInventory(player, 9, "Join faction " + faction.factionName);

        ItemStack requestToJoin = new ItemStack(Material.GREEN_WOOL);
        ItemMeta requestToJoinMeta = requestToJoin.getItemMeta();
        requestToJoinMeta.setDisplayName("Request to join");
        requestToJoin.setItemMeta(requestToJoinMeta);

        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName("Cancel");
        cancel.setItemMeta(cancelMeta);

        inventory.setItem(3, requestToJoin);
        inventory.setItem(5, cancel);

        player.openInventory(inventory);
    }

    public void createFaction(Player player)
    {
        Faction faction;
        if(createFactions.containsKey(player.getUniqueId())){
            faction = createFactions.get(player.getUniqueId());
        }else{
            faction = new Faction("!" + player.getName(), player.getUniqueId());
            createFactions.put(player.getUniqueId(), faction);
        }

        Inventory inventory = Bukkit.createInventory(player, 18, "Create faction");

        ItemStack factionName = new ItemStack(Material.NAME_TAG);
        ItemMeta factionNameMeta = factionName.getItemMeta();
        factionNameMeta.setDisplayName(faction.factionName);
        factionName.setItemMeta(factionNameMeta);

        ItemStack factionColor = new ItemStack(colors.getMaterialWithChatColor(faction.color));
        ItemMeta factionColorMeta = factionColor.getItemMeta();
        factionColorMeta.setDisplayName(faction.color + colors.getColorNameWithChatColor(faction.color));
        factionColor.setItemMeta(factionColorMeta);

        ItemStack discardFaction = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta discardFactionMeta = discardFaction.getItemMeta();
        discardFactionMeta.setDisplayName("Discard");
        discardFaction.setItemMeta(discardFactionMeta);

        inventory.setItem(17, discardFaction);
        inventory.setItem(2, factionName);
        inventory.setItem(6, factionColor);

        if(!faction.factionName.equals("!" + player.getName())){
            ItemStack createFaction = new ItemStack(Material.GREEN_WOOL);
            ItemMeta createFactionMeta = createFaction.getItemMeta();
            createFactionMeta.setDisplayName("Create");
            createFaction.setItemMeta(createFactionMeta);
            inventory.setItem(13, createFaction);
        }

        player.openInventory(inventory);
    }

    public void setFactionColor(Player player)
    {
        Inventory inventory = Bukkit.createInventory(player, 27, "Set faction color");

        ItemStack colorBlack = new ItemStack(Material.BLACK_WOOL);
        ItemMeta colorBlackMeta = colorBlack.getItemMeta();
        colorBlackMeta.setDisplayName(ChatColor.BLACK + "Black");
        colorBlack.setItemMeta(colorBlackMeta);

        ItemStack colorRed = new ItemStack(Material.RED_WOOL);
        ItemMeta colorRedMeta = colorRed.getItemMeta();
        colorRedMeta.setDisplayName(ChatColor.RED + "Red");
        colorRed.setItemMeta(colorRedMeta);

        ItemStack colorAqua = new ItemStack(Material.LIGHT_BLUE_WOOL);
        ItemMeta colorAquaMeta = colorAqua.getItemMeta();
        colorAquaMeta.setDisplayName(ChatColor.AQUA + "Aqua");
        colorAqua.setItemMeta(colorAquaMeta);

        ItemStack colorBlue = new ItemStack(Material.BLUE_WOOL);
        ItemMeta colorBlueMeta = colorBlue.getItemMeta();
        colorBlueMeta.setDisplayName(ChatColor.BLUE + "Blue");
        colorBlue.setItemMeta(colorBlueMeta);

        ItemStack colorDarkAqua = new ItemStack(Material.CYAN_WOOL);
        ItemMeta colorDarkAquaMeta = colorDarkAqua.getItemMeta();
        colorDarkAquaMeta.setDisplayName(ChatColor.DARK_AQUA + "Dark_Aqua");
        colorDarkAqua.setItemMeta(colorDarkAquaMeta);

        ItemStack colorDarkBlue = new ItemStack(Material.BLUE_WOOL);
        ItemMeta colorDarkBlueMeta = colorDarkBlue.getItemMeta();
        colorDarkBlueMeta.setDisplayName(ChatColor.DARK_BLUE + "Dark_Blue");
        colorDarkBlue.setItemMeta(colorDarkBlueMeta);

        ItemStack colorDarkGray = new ItemStack(Material.GRAY_WOOL);
        ItemMeta colorDarkGrayMeta = colorDarkGray.getItemMeta();
        colorDarkGrayMeta.setDisplayName(ChatColor.DARK_GRAY + "Dark_Gray");
        colorDarkGray.setItemMeta(colorDarkGrayMeta);

        ItemStack colorDarkGreen = new ItemStack(Material.GREEN_WOOL);
        ItemMeta colorDarkGreenMeta = colorDarkGreen.getItemMeta();
        colorDarkGreenMeta.setDisplayName(ChatColor.DARK_GREEN + "Dark_Green");
        colorDarkGreen.setItemMeta(colorDarkGreenMeta);

        ItemStack colorDarkPurple = new ItemStack(Material.PURPLE_WOOL);
        ItemMeta colorDarkPurpleMeta = colorDarkPurple.getItemMeta();
        colorDarkPurpleMeta.setDisplayName(ChatColor.DARK_PURPLE + "Dark_Purple");
        colorDarkPurple.setItemMeta(colorDarkPurpleMeta);

        ItemStack colorDarkRed = new ItemStack(Material.RED_WOOL);
        ItemMeta colorDarkRedMeta = colorDarkRed.getItemMeta();
        colorDarkRedMeta.setDisplayName(ChatColor.DARK_RED + "Dark_Red");
        colorDarkRed.setItemMeta(colorDarkRedMeta);

        ItemStack colorGold = new ItemStack(Material.ORANGE_WOOL);
        ItemMeta colorGoldMeta = colorGold.getItemMeta();
        colorGoldMeta.setDisplayName(ChatColor.GOLD + "Gold");
        colorGold.setItemMeta(colorGoldMeta);

        ItemStack colorGray = new ItemStack(Material.LIGHT_GRAY_WOOL);
        ItemMeta colorGrayMeta = colorGray.getItemMeta();
        colorGrayMeta.setDisplayName(ChatColor.GRAY + "Gray");
        colorGray.setItemMeta(colorGrayMeta);

        ItemStack colorGreen= new ItemStack(Material.LIME_WOOL);
        ItemMeta colorGreenMeta = colorGreen.getItemMeta();
        colorGreenMeta.setDisplayName(ChatColor.GREEN + "Green");
        colorGreen.setItemMeta(colorGreenMeta);

        ItemStack colorLightPurple = new ItemStack(Material.MAGENTA_WOOL);
        ItemMeta colorLightPurpleMeta = colorLightPurple.getItemMeta();
        colorLightPurpleMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Light_Purple");
        colorLightPurple.setItemMeta(colorLightPurpleMeta);

        ItemStack colorWhite = new ItemStack(Material.WHITE_WOOL);
        ItemMeta colorWhiteMeta = colorWhite.getItemMeta();
        colorWhiteMeta.setDisplayName(ChatColor.WHITE + "White");
        colorWhite.setItemMeta(colorWhiteMeta);

        ItemStack colorYellow = new ItemStack(Material.YELLOW_WOOL);
        ItemMeta colorYellowMeta = colorYellow.getItemMeta();
        colorYellowMeta.setDisplayName(ChatColor.YELLOW + "Yellow");
        colorYellow.setItemMeta(colorYellowMeta);

        inventory.setItem(1, colorBlack);
        inventory.setItem(2, colorRed);
        inventory.setItem(3, colorAqua);
        inventory.setItem(4, colorBlue);
        inventory.setItem(5, colorDarkAqua);
        inventory.setItem(6, colorDarkBlue);
        inventory.setItem(7, colorDarkGray);
        inventory.setItem(10, colorDarkGreen);
        inventory.setItem(11, colorDarkPurple);
        inventory.setItem(12, colorDarkRed);
        inventory.setItem(13, colorGold);
        inventory.setItem(14, colorGray);
        inventory.setItem(15, colorGreen);
        inventory.setItem(16, colorLightPurple);
        inventory.setItem(21, colorWhite);
        inventory.setItem(23, colorYellow);

        player.openInventory(inventory);
    }

    public void faction(Player player)
    {
        Faction faction = factions.get(players.get(player.getUniqueId()));
        Inventory inventory = Bukkit.createInventory(player, 18, "Faction " + faction.factionName);

        ItemStack factionName = new ItemStack(Material.NAME_TAG);
        ItemMeta factionNameMeta = factionName.getItemMeta();
        factionNameMeta.setDisplayName("Faction name");
        factionNameMeta.setLore(Collections.singletonList(faction.factionName));
        factionName.setItemMeta(factionNameMeta);

        ItemStack factionColor = new ItemStack(colors.getMaterialWithChatColor(faction.color));
        ItemMeta factionColorMeta = factionColor.getItemMeta();
        factionColorMeta.setDisplayName("Faction color");
        factionColorMeta.setLore(Collections.singletonList(faction.color + colors.getColorNameWithChatColor(faction.color)));
        factionColor.setItemMeta(factionColorMeta);

        ItemStack factionOwner = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta factionOwnerMeta = factionOwner.getItemMeta();
        factionOwnerMeta.setDisplayName("Owner");
        factionOwnerMeta.setLore(Collections.singletonList(getPlayerNameFromUUID(faction.owner.toString())));
        factionOwner.setItemMeta(factionOwnerMeta);

        ItemStack factionMembers = new ItemStack(Material.OAK_SIGN);
        ItemMeta factionMembersMeta = factionMembers.getItemMeta();
        factionMembersMeta.setDisplayName("Members");

        ArrayList<String> members = new ArrayList<>();
        for(Map.Entry<UUID, String> entry : faction.members.entrySet()){
            members.add("- " + getPlayerNameFromUUID(entry.getKey().toString()) + " (" + entry.getValue() + ")");
        }
        factionMembersMeta.setLore(members);
        factionMembers.setItemMeta(factionMembersMeta);

        if(faction.owner == player.getUniqueId()){
            ItemStack deleteFaction = new ItemStack(Material.LAVA_BUCKET);
            ItemMeta deleteFactionMeta = deleteFaction.getItemMeta();
            deleteFactionMeta.setDisplayName("Delete faction");
            deleteFaction.setItemMeta(deleteFactionMeta);
            inventory.setItem(17, deleteFaction);
        }else{
            ItemStack factionLeave = new ItemStack(Material.LAVA_BUCKET);
            ItemMeta factionLeaveMeta = factionLeave.getItemMeta();
            factionLeaveMeta.setDisplayName("Leave faction");
            factionLeave.setItemMeta(factionLeaveMeta);
            inventory.setItem(17, factionLeave);
        }

        inventory.setItem(1, factionName);
        inventory.setItem(3, factionColor);
        inventory.setItem(5, factionOwner);
        inventory.setItem(7,factionMembers);

        player.openInventory(inventory);
    }

    public void memberList(Player player)
    {
        ArrayList<Inventory> memberListInventory = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        for(Map.Entry<UUID, String> entry : factions.get(players.get(player.getUniqueId())).members.entrySet()){
            Player member = Bukkit.getPlayer(entry.getKey());
            if(member != null){ names.add(member.getName()); }
        }

        double amount = factions.get(players.get(player.getUniqueId())).members.size();
        double inventoryAmount = amount / 45;
        inventoryAmount = Math.ceil(inventoryAmount);
        int index = 0;

        for(int i = 0; i < inventoryAmount; i++){
            Inventory inventory = Bukkit.createInventory(player, 54, "Member list " + i);
            int timesToLoop = Math.min(factions.size() - (i * 45), 45);

            for (int j = 0; j < timesToLoop ; j++){
                ItemStack faction = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta factionMeta = faction.getItemMeta();
                factionMeta.setDisplayName(names.get(index));
                faction.setItemMeta(factionMeta);

                inventory.addItem(faction);
                index++;
            }

            ItemStack nextPage = new ItemStack(Material.STONE_BUTTON);
            ItemMeta nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName("Next");
            nextPage.setItemMeta(nextPageMeta);

            ItemStack previousPage = new ItemStack(Material.STONE_BUTTON);
            ItemMeta previousPageMeta = previousPage.getItemMeta();
            previousPageMeta.setDisplayName("Next");
            previousPage.setItemMeta(previousPageMeta);

            if(i == 0){
                if(amount > 45){
                    inventory.setItem(53, nextPage);
                }
            } else if (i == inventoryAmount) {
                inventory.setItem(46, previousPage);
            }else {
                inventory.setItem(53, nextPage);
                inventory.setItem(46, previousPage);
            }

            memberListInventory.add(inventory);
        }
        InventoryList inventoryList = new InventoryList(memberListInventory);
        Simple_Factions.factionListInventories.put(player.getUniqueId(), inventoryList);

        player.openInventory(memberListInventory.get(0));
    }

    public void memberInfo(Player player, Player member)
    {
        Faction faction = factions.get(players.get(player.getUniqueId()));
        Inventory inventory = Bukkit.createInventory(player, 9, "Faction member " + member.getName());

        ItemStack memberName = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta memberNameMeta = memberName.getItemMeta();
        memberNameMeta.setDisplayName(member.getName());
        memberName.setItemMeta(memberNameMeta);

        ItemStack memberRank = new ItemStack(Material.NAME_TAG);
        ItemMeta memberRankMeta = memberRank.getItemMeta();
        memberRankMeta.setDisplayName(faction.members.get(member.getUniqueId()));
        memberRank.setItemMeta(memberRankMeta);

        if(faction.owner == player.getUniqueId()){
            ItemStack kickMember = new ItemStack(Material.LAVA_BUCKET);
            ItemMeta kickMemberMeta = kickMember.getItemMeta();
            kickMemberMeta.setDisplayName("Kick member");
            kickMember.setItemMeta(kickMemberMeta);

            ItemStack makeOwner = new ItemStack(Material.EGG);
            ItemMeta makeOwnerMeta = makeOwner.getItemMeta();
            makeOwnerMeta.setDisplayName("Make owner");
            makeOwner.setItemMeta(makeOwnerMeta);

            inventory.setItem(1, memberRank);
            inventory.setItem(3, memberName);
            inventory.setItem(5, makeOwner);
            inventory.setItem(7, kickMember);

        }else{
            inventory.setItem(6, memberRank);
            inventory.setItem(2, memberName);
        }

        player.openInventory(inventory);
    }

    public void deleteFaction(Player player)
    {
        Faction faction = factions.get(players.get(player.getUniqueId()));
        Inventory inventory = Bukkit.createInventory(player, 9, "Delete faction " + faction.factionName);

        ItemStack delete = new ItemStack(Material.GREEN_WOOL);
        ItemMeta deleteMeta = delete.getItemMeta();
        deleteMeta.setDisplayName("Delete Faction");
        delete.setItemMeta(deleteMeta);

        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName("cancel");
        cancel.setItemMeta(cancelMeta);

        inventory.setItem(2, delete);
        inventory.setItem(6, cancel);

        player.openInventory(inventory);
    }

    public void leaveFaction(Player player)
    {
        Faction faction = factions.get(players.get(player.getUniqueId()));
        Inventory inventory = Bukkit.createInventory(player, 9, "Leave faction " + faction.factionName);

        ItemStack leave = new ItemStack(Material.GREEN_WOOL);
        ItemMeta leaveMeta = leave.getItemMeta();
        leaveMeta.setDisplayName("Leave Faction");
        leave.setItemMeta(leaveMeta);

        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName("cancel");
        cancel.setItemMeta(cancelMeta);

        inventory.setItem(2, leave);
        inventory.setItem(6, cancel);

        player.openInventory(inventory);
    }

    public String getPlayerNameFromUUID(String uuidString)
    {
        UUID uuid = UUID.fromString(uuidString);

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            return player.getName();
        } else {
            return "Unknown Player";
        }
    }
}