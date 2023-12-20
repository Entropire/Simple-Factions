package me.entropire.simplefactions;

import me.entropire.simplefactions.Commands.FactionCommand;
import me.entropire.simplefactions.Listeners.ChatUse;
import me.entropire.simplefactions.Listeners.FactionGUI;
import me.entropire.simplefactions.Listeners.OnJoinLeave;
import me.entropire.simplefactions.Libraries.Colors;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

import static java.awt.Color.RED;

public final class Simple_Factions extends JavaPlugin {
    Colors colors = new Colors();

    public static HashMap<String, Faction> factions = new HashMap<>();
    public static HashMap<UUID, String> players = new HashMap<>();
    public static HashMap<UUID, Object[]> pendingInvites = new HashMap<>();

    public static HashMap<UUID, Faction> createFactions = new HashMap<>();

    public static HashMap<UUID, String> namingFactions = new HashMap<>();

    public static HashMap<UUID, UUID> joinRequests = new HashMap<>();

    public static HashMap<UUID, InventoryList> memberListInventories = new HashMap<>();

    public static HashMap<UUID, InventoryList> factionListInventories = new HashMap<>();
    private final String pluginFolderPath = getDataFolder().getAbsolutePath();

    @Override
    public void onEnable()
    {
        loadConfiguration();

        createFile(pluginFolderPath + "/Factions.yml");
        createFile(pluginFolderPath + "/Players.yml");

        if (!readFile(pluginFolderPath + "/Factions.yml").isEmpty()) {
            loadFactions();
            loadPlayers();
        }


        getServer().getPluginManager().registerEvents(new OnJoinLeave(), this);
        getServer().getPluginManager().registerEvents(new ChatUse(), this);
        getServer().getPluginManager().registerEvents(new FactionGUI(), this);

        getCommand("faction").setExecutor(new FactionCommand());
    }

    @Override
    public void onDisable()
    {
        saveFactions();
        savePlayers();
    }

    private void loadConfiguration()
    {
        File f = new File("plugins/ReferredBy/Factions.yml");
        if(!f.exists()) { createConfig(); }
    }
    private void createConfig()
    {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void savePlayers()
    {
        Map<String, String> playersData = new HashMap<>();

        for (Map.Entry<UUID, String> entry2 : players.entrySet()) {
            playersData.put(entry2.getKey().toString(), entry2.getValue());
        }

        Yaml yaml = new Yaml();
        String yamlPlayersData = yaml.dump(playersData);

        try {
            FileWriter myWriter = new FileWriter( pluginFolderPath + "/Players.yml");
            myWriter.write(yamlPlayersData);
            myWriter.close();
        } catch (IOException e) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "An error occurred. \n" + e.getMessage());
        }
    }

    public void saveFactions()
    {
        Map<String, Object> factionsData = new HashMap<>();

        for(Map.Entry<String, Faction> entry : factions.entrySet()){
            Map<String, Object> factionData = new HashMap<>();

            Faction faction = entry.getValue();
            String factionName = faction.factionName;

            Map<UUID, String> members = faction.members;

            Map<String, String> membersStringUUID = new HashMap<>();
            for (Map.Entry<UUID, String> entry2 : members.entrySet()) {
                membersStringUUID.put(entry2.getKey().toString(), entry2.getValue());
            }

            factionData.put("factionName", factionName);
            factionData.put("color", colors.getColorNameWithChatColor(faction.color));
            factionData.put("owner", faction.owner.toString());
            factionData.put("members", membersStringUUID);

            factionsData.put(factionName, factionData);
        }

        Yaml yaml = new Yaml();
        String yamlFactionsData = yaml.dump(factionsData);

        try {
            FileWriter myWriter = new FileWriter( pluginFolderPath + "/Factions.yml");
            myWriter.write(yamlFactionsData);
            myWriter.close();
        } catch (IOException e) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "An error occurred. \n" + e.getMessage());
        }
    }

    public void loadFactions()
    {
        try {
            FileReader reader = new FileReader(pluginFolderPath + "/Factions.yml");
            Yaml yaml = new Yaml();

            Map<String, Map<String, Object>> factionsData = yaml.load(reader);

            for (Map.Entry<String, Map<String, Object>> entry : factionsData.entrySet()) {
                Map<String, Object> factionData = entry.getValue();

                String factionNameValue = (String) factionData.get("factionName");
                String colorValue = (String) factionData.get("color");
                String ownerValue = (String) factionData.get("owner");
                Map<String, String> membersStringUUID = (Map<String, String>) factionData.get("members");

                UUID ownerUUID = UUID.fromString(ownerValue);

                Map<UUID, String> members = new HashMap<>();
                for (Map.Entry<String, String> memberEntry : membersStringUUID.entrySet()) {
                    UUID memberUUID = UUID.fromString(memberEntry.getKey());
                    members.put(memberUUID, memberEntry.getValue());
                }

                Faction faction = new Faction(factionNameValue, ownerUUID);
                factions.put(factionNameValue, faction);
                factions.get(factionNameValue).consoleSetColor(colorValue);
                factions.get(factionNameValue).consoleSetMembers(members);
            }

            reader.close();
        } catch (IOException e) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "An error occurred while loading factions. \n" + e.getMessage());
        }
    }

    public void loadPlayers()
    {
        try {
            FileReader reader = new FileReader(pluginFolderPath + "/Players.yml");
            Yaml yaml = new Yaml();

            HashMap<String, String> yamlPlayersData = yaml.load(reader);

            HashMap<UUID, String> playersData = new HashMap<>();
            for (Map.Entry<String, String> entry : yamlPlayersData.entrySet()) {
                playersData.put(UUID.fromString(entry.getKey()), entry.getValue());
            }

            players = playersData;

            reader.close();
        } catch (IOException e) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "An error occurred while loading players. \n" + e.getMessage());
        }
    }

    public void createFile(String fileName)
    {
        try {
            File myObj = new File(fileName);
            if (myObj.createNewFile()) {
                getServer().getConsoleSender().sendMessage(RED + "File created: " + myObj.getName());
            } else {
                getServer().getConsoleSender().sendMessage(RED + "File already exists.");
            }
        } catch (IOException e) {
            getServer().getConsoleSender().sendMessage(RED + "An error occurred.");
        }
    }

    public  void writeFile(String fileName, String message)
    {
        try {
            FileWriter myWriter = new FileWriter(fileName);
            myWriter.write(message);
            myWriter.close();
        } catch (IOException e) {
            getServer().getConsoleSender().sendMessage(RED + "An error occurred.");
        }
    }

    public String readFile(String fileName)
    {
        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                return myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            getServer().getConsoleSender().sendMessage(RED + "An error occurred.");
        }
        return "";
    }
}
