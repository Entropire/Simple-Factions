package me.entropire.simple_factions;

import me.entropire.simple_factions.objects.*;
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

public class GuiManager
{
    private final Simple_Factions simpleFactionsPlugin;
    private final Colors colors = new Colors();
    private final FactionManager factionManager;

    public GuiManager(Simple_Factions simpleFactionsPlugin)
    {
        this.factionManager = new FactionManager(simpleFactionsPlugin);
        this.simpleFactionsPlugin = simpleFactionsPlugin;
    }

    public void SimpleFaction(Player player)
    {
        Gui gui = new Gui("Simple-Factions", 27);

        gui.addButton(11, "Create new faction", Material.ANVIL, "Create a new faction.", (btn, event) -> {
                    CreateFaction((Player)event.getView().getPlayer());
                });

        gui.addButton(15, "Join faction", Material.NAME_TAG, "Join a faction.", (btn, event) -> {
            try {
                FactionList((Player)event.getView().getPlayer(), 0);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        player.openInventory(gui.Create());
    }

    public void FactionList(Player player, int pageNumber) throws SQLException
    {
        Gui gui = new Gui("Faction page " + pageNumber, 54);
        ArrayList<String> factions = simpleFactionsPlugin.factionDatabase.getFactions();

        if(!factions.isEmpty())
        {
            int index = 0;
            for(int i = 45 * pageNumber, j = Math.min(45 * (pageNumber + 1), factions.size()); i < j; i++)
            {
                gui.addButton(index, factions.get(i), Material.PLAYER_HEAD, "", (btn, event) -> {
                    try {
                        FactionInfo((Player)event.getView().getPlayer() , btn.getItemMeta().getDisplayName());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                index++;
            }
        }

        float pageAmount = (float) factions.size() / 45;

        if(pageNumber < pageAmount - 1)
        {
            gui.addButton(53, "Next", Material.STONE_BUTTON, "Go to the next page.", (btn, event) -> {
                String inventoryName = event.getView().getTitle().replace("Factions page ", "");
                int eventPageNumber = Integer.parseInt(inventoryName) + 1;
                try {
                    FactionList((Player) event.getView().getPlayer(), eventPageNumber);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        else
        {
            gui.addButton(53, "", Material.GRAY_STAINED_GLASS_PANE, "", null);
        }

        if(pageNumber > 0)
        {
            gui.addButton(45, "Previous", Material.STONE_BUTTON, "Go to the previous page.", (btn, event) -> {
                String inventoryName = event.getView().getTitle().replace("Factions page ", "");
                int eventPageNumber = Integer.parseInt(inventoryName) - 1;
                try {
                    FactionList((Player) event.getView().getPlayer(), eventPageNumber);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        else
        {
            gui.addButton(45, "", Material.GRAY_STAINED_GLASS_PANE, "", null);
        }

        gui.addButton(49, "Leave", Material.RED_WOOL, "Go back to the main menu.", (btn, event) -> {
            SimpleFaction((Player)event.getView().getPlayer());
        });

        gui.addButton(46, "", Material.GRAY_STAINED_GLASS_PANE, "", null);
        gui.addButton(47, "", Material.GRAY_STAINED_GLASS_PANE, "", null);
        gui.addButton(48, "", Material.GRAY_STAINED_GLASS_PANE, "", null);
        gui.addButton(50, "", Material.GRAY_STAINED_GLASS_PANE, "", null);
        gui.addButton(51, "", Material.GRAY_STAINED_GLASS_PANE, "", null);
        gui.addButton(52, "", Material.GRAY_STAINED_GLASS_PANE, "", null);

        player.openInventory(gui.Create());
    }

    public void FactionInfo(Player player, String factionName) throws SQLException
    {
        Gui gui = new Gui("Info of " + factionName, 27);

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


        gui.addButton(2, "Faction name", Material.NAME_TAG, factionName, null);
        gui.addButton(4, "Faction owner", Material.PLAYER_HEAD, ownerName, null);
        gui.addButton(6, "Faction members", Material.OAK_SIGN, top10Members, null);
        gui.addButton(21, "Join", Material.GREEN_WOOL, "Request to join this faction.", (btn, event) -> {
            String EventFactionName = event.getView().getTitle().replace("Info of ", "");
            try
            {
                factionManager.join((Player)event.getView().getPlayer(), EventFactionName);
            } catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        });

        gui.addButton(23, "Leave", Material.RED_WOOL, "Go back to the previous page.", (btn, event) -> {
            try {
                FactionList((Player)event.getView().getPlayer(), 0);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        player.openInventory(gui.Create());
    }

    public void CreateFaction(Player player)
    {
        Gui gui = new Gui("New faction", 27);

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

        gui.addButton(2, "Faction name", Material.NAME_TAG, factionName, (btn, event) -> {
            SetFactionName((Player)event.getView().getPlayer());
        });
        gui.addButton(6, "Faction color", colors.getMaterialWithChatColor(factionColor), factionColor + colors.getColorNameWithChatColor(factionColor), (btn, event) -> {
            SetFactionColor((Player)event.getView().getPlayer());
        });
        gui.addButton(23, "Discard", Material.RED_WOOL, "Discard your faction creation.", (btn, event) -> {
            factionManager.DeleteFactionCreation((Player)event.getView().getPlayer());
            event.getView().getPlayer().closeInventory();
        });

        if(!factionName.contains("New Faction"))
        {
            gui.addButton(21, "Create", Material.GREEN_WOOL, "Create your new faction.", (btn, event) -> {
                try {
                    factionManager.create((Player)event.getView().getPlayer());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        player.openInventory(gui.Create());
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
        Gui gui = new Gui("Set faction color", 27);

        int i = 0;
        List<Integer> slots = Arrays.asList(1,2,3,4,5,6,7,10,11,12,13,14,15,16,21,23);
        for (String colorName : colors.colorName)
        {
            gui.addButton(slots.get(i), colors.getChatColorWithColorName(colorName) + colorName, colors.getMaterialWithColorName(colorName), "" , (btn, event) -> {
                String eventColorName = btn.getItemMeta().getDisplayName();
                ChatColor color = colors.getChatColorWithColorName(ChatColor.stripColor(eventColorName));
                simpleFactionsPlugin.createFactions.get(event.getView().getPlayer().getUniqueId()).setColor(color);
                CreateFaction((Player)event.getView().getPlayer());
            });
            i++;
        }

        player.openInventory(gui.Create());
    }

    public void Faction(Player player, Faction faction) throws SQLException {
        Gui gui = new Gui(faction.getName(), 27);

        gui.addButton(10, "Faction name", Material.NAME_TAG, faction.getName(), (btn, event) -> {
            ChangeFactionName((Player)event.getView().getPlayer());
        });
        gui.addButton(12, "Faction color", colors.getMaterialWithChatColor(faction.getColor()), faction.getColor() + colors.getColorNameWithChatColor(faction.getColor()), (btn, event) -> {
            ChangeFactionColor((Player)event.getView().getPlayer());
        });
        gui.addButton(14, "Owner", Material.PLAYER_HEAD, simpleFactionsPlugin.playerDatabase.getPlayerName(faction.getOwner().toString()), null);
        ArrayList<String> members = new ArrayList<>();
        for (int i = 0; i < Math.min(9, faction.getMembers().size()); i++) {
            members.add(faction.getMembers().get(i));
        }
        gui.addButton(16, "Members", Material.OAK_SIGN, members, null);
        if(faction.getOwner().equals(player.getUniqueId()))
        {
            gui.addButton(18, "Invite Player", Material.PAPER,  "", null);
            gui.addButton(26, "Delete Faction", Material.RED_WOOL,  "", null);
        }
        else
        {
            gui.addButton(26, "Leave Faction", Material.RED_WOOL,  "", null);
        }

        player.openInventory(gui.Create());
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
        Gui gui = new Gui("Change faction color" , 27);

        int i = 0;
        List<Integer> slots = Arrays.asList(1,2,3,4,5,6,7,10,11,12,13,14,15,16,21,23);
        for (String colorName : colors.colorName)
        {
            gui.addButton(slots.get(i),colors.getChatColorWithColorName(colorName) + colorName, colors.getMaterialWithColorName(colorName), "", (btn, event) -> {
                String eventColorName  = btn.getItemMeta().getDisplayName();

                Faction faction;
                try {
                    factionManager.modifyColor((Player)event.getView().getPlayer(), ChatColor.stripColor(eventColorName));
                    int factionId = simpleFactionsPlugin.playerDatabase.getFactionId((Player)event.getView().getPlayer());
                    faction  = simpleFactionsPlugin.factionDatabase.getFactionDataById(factionId);
                    Faction((Player)event.getView().getPlayer(), faction);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            i++;
        }

        player.openInventory(gui.Create());
    }
}