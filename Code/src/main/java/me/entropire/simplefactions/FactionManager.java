package me.entropire.simplefactions;

import me.entropire.simplefactions.objects.Colors;
import me.entropire.simplefactions.objects.Faction;
import me.entropire.simplefactions.objects.Invite;
import me.entropire.simplefactions.objects.Join;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import static org.bukkit.ChatColor.*;


@SuppressWarnings("deprecation")
public class FactionManager
{
    private final Colors colors = new Colors();

    private final Simple_Factions simpleFactionsPlugin;

    public FactionManager(Simple_Factions simpleFactionsPlugin)
    {
        this.simpleFactionsPlugin = simpleFactionsPlugin;
    }

    public void create(Player player, String factionName) throws SQLException
    {
        if(simpleFactionsPlugin.playerDatabase.hasFaction(player))
        {
            player.sendMessage(RED + "You are already in a faction.");
            return;
        }
        if(simpleFactionsPlugin.factionDatabase.factionExistsByName(factionName))
        {
            player.sendMessage(RED + "There already is a faction with the name '" + factionName + "'.");
            return;
        }

        ArrayList<String> members = new ArrayList<>();
        members.add(player.getName());
        Faction faction = new Faction(0, factionName, WHITE, player.getUniqueId(), members);

        simpleFactionsPlugin.factionDatabase.addFaction(faction);
        simpleFactionsPlugin.playerDatabase.updateFactionWithPlayerName(player.getName(), simpleFactionsPlugin.factionDatabase.getFactionDataByName(factionName).id());

        changePlayerDisplayName(player, faction.color() + "[" + faction.name() + "] " + player.getName());

        player.sendMessage(GREEN + "New faction " + factionName + " created.");
    }

    public void create(UUID player, String factionName) throws SQLException
    {
        ArrayList<String> members = new ArrayList<>();
        members.add(player.toString());
        Faction faction = new Faction(0, factionName, WHITE, player, members);

        simpleFactionsPlugin.factionDatabase.addFaction(faction);
        simpleFactionsPlugin.playerDatabase.updateFactionWithPlayerName(player.toString(), simpleFactionsPlugin.factionDatabase.getFactionDataByName(factionName).id());
    }

    public void delete(Player player) throws SQLException
    {
        if(!simpleFactionsPlugin.playerDatabase.hasFaction(player))
        {
            player.sendMessage(RED + "You are not in a faction.");
            return;
        }

        int factionId = simpleFactionsPlugin.playerDatabase.getFactionId(player);
        Faction faction = simpleFactionsPlugin.factionDatabase.getFactionDataById(factionId);

        if (faction == null)
        {
            player.sendMessage(RED + "Faction data not found.");
            return;
        }

        if(!faction.owner().equals(player.getUniqueId()))
        {
            player.sendMessage(RED + "Only the owner of a faction can delete it.");
            return;
        }

        ArrayList<String> members = faction.members();
        for (String memberName : members) {
            UUID memberUUID = simpleFactionsPlugin.playerDatabase.getPlayerUUID(memberName);
            Player member = Bukkit.getPlayer(memberUUID);

            simpleFactionsPlugin.playerDatabase.updateFactionWithPlayerUUID(memberUUID, 0);

            if(member != null)
            {
                changePlayerDisplayName(member, member.getName());
            }
        }
        simpleFactionsPlugin.factionDatabase.deleteFaction(factionId);

        player.sendMessage(RED + "You have delete your faction " + faction.name());
    }

    public void list(Player player) throws SQLException
    {
        ArrayList<String> factionsNames = simpleFactionsPlugin.factionDatabase.getFactions();
        player.sendMessage("Factions: ");
        for (String factionsName : factionsNames)
        {
            player.sendMessage("- " + factionsName);
        }
    }

    public void members(Player player, String factionName) throws SQLException
    {
        ArrayList<String> members;
        if(factionName == null)
        {
            if(simpleFactionsPlugin.playerDatabase.hasFaction(player))
            {
                int factionId = simpleFactionsPlugin.playerDatabase.getFactionId(player);
                members = simpleFactionsPlugin.factionDatabase.getFactionDataById(factionId).members();
            }
            else
            {
                player.sendMessage(RED + "You are not in a faction.");
                return;
            }
        }
        else if  (simpleFactionsPlugin.factionDatabase.factionExistsByName(factionName))
        {
            members = simpleFactionsPlugin.factionDatabase.getFactionDataByName(factionName).members();
        }
        else
        {
            player.sendMessage(RED + "faction " + factionName + " does not  exists.");
            return;
        }

        player.sendMessage("Members: ");
        for (String member : members)
        {
            player.sendMessage(" -" + member);
        }
    }

    public void owner(Player player, String factionName) throws SQLException
    {
        Faction faction;
        if(factionName == null)
        {
            if(simpleFactionsPlugin.playerDatabase.hasFaction(player))
            {
                int factionId = simpleFactionsPlugin.playerDatabase.getFactionId(player);
                faction= simpleFactionsPlugin.factionDatabase.getFactionDataById(factionId);
            }
            else
            {
                player.sendMessage(RED + "you are not in a faction");
                return;
            }
        }
        else if  (simpleFactionsPlugin.factionDatabase.factionExistsByName(factionName))
        {
            faction = simpleFactionsPlugin.factionDatabase.getFactionDataByName(factionName);
        }
        else
        {
            player.sendMessage(RED + "faction " + factionName + " does not  exists.");
            return;
        }

        String ownerUUID = faction.owner().toString();
        String ownerName = simpleFactionsPlugin.playerDatabase.getPlayerName(ownerUUID);
        player.sendMessage("Owner of " + faction.name() + ": " + ownerName);
    }


    public void leave(Player player) throws SQLException
    {
        if(!simpleFactionsPlugin.playerDatabase.hasFaction(player))
        {
            player.sendMessage(RED + "You are not in a faction.");
            return;
        }

        int factionId = simpleFactionsPlugin.playerDatabase.getFactionId(player);
        Faction factionData = simpleFactionsPlugin.factionDatabase.getFactionDataById(factionId);
        if(factionData.owner().equals(player.getUniqueId()))
        {
            player.sendMessage(RED + "As owner you van not leave the faction.");
            return;
        }

        simpleFactionsPlugin.factionDatabase.updateFactionMembers(factionId, player.getName(), false);
        simpleFactionsPlugin.playerDatabase.updateFactionWithPlayerName(player.getName(), 0);

        changePlayerDisplayName(player, player.getName());
        player.sendMessage(GREEN + "You left the faction: " + factionData.name());
    }

    public void kick(Player player, String playerName) throws SQLException
    {
        if(!simpleFactionsPlugin.playerDatabase.hasFaction(player))
        {
            player.sendMessage(RED + "You must be in a faction to kick somebody.");
            return;
        }

        int factionId = simpleFactionsPlugin.playerDatabase.getFactionId(player);
        Faction factionData = simpleFactionsPlugin.factionDatabase.getFactionDataById(factionId);

        if(!factionData.owner().equals(player.getUniqueId()))
        {
            player.sendMessage(RED + "Only the owner of the faction can kick some wane.");
            return;
        }
        if(!factionData.members().contains(playerName))
        {
            player.sendMessage(RED + "This player is not in your faction.");
            return;
        }
        UUID playerToKickId = simpleFactionsPlugin.playerDatabase.getPlayerUUID(playerName);
        if(factionData.owner().equals(playerToKickId))
        {
            player.sendMessage(RED + "The owner can not be kicked from the faction.");
            return;
        }
        Player playerToKick = player.getServer().getPlayer(playerName);

        simpleFactionsPlugin.factionDatabase.updateFactionMembers(factionId, playerName, false);
        simpleFactionsPlugin.playerDatabase.updateFactionWithPlayerName(playerName, 0);

        player.sendMessage(YELLOW + "You have kicked " + playerName + " from your faction.");
        if(playerToKick != null){
            changePlayerDisplayName(playerToKick, playerToKick.getName());
        }
    }

    public void invite(Player player, String invitedPlayerName) throws SQLException
    {
        if(!simpleFactionsPlugin.playerDatabase.hasFaction(player))
        {
            player.sendMessage(RED + "You must be in a faction to invite some wane");
            return;
        }

        int factionId = simpleFactionsPlugin.playerDatabase.getFactionId(player);
        Faction faction = simpleFactionsPlugin.factionDatabase.getFactionDataById(factionId);

        if(!faction.owner().equals(player.getUniqueId()))
        {
            player.sendMessage(RED  + "Only the owner can invite player to the faction." );
            return;
        }

        Player invitedPlayer = Bukkit.getServer().getPlayer(invitedPlayerName);
        if (invitedPlayer == null || !invitedPlayer.isOnline()) {
            player.sendMessage(ChatColor.RED + (invitedPlayer == null ? invitedPlayerName + " is not a player." : invitedPlayerName + " is not online."));
            return;
        }
        if(simpleFactionsPlugin.playerDatabase.hasFaction(invitedPlayer))
        {
            player.sendMessage(RED  + invitedPlayerName + " is already in a faction." );
            return;
        }
        if(simpleFactionsPlugin.invites.containsKey(invitedPlayer.getUniqueId()))
        {
            player.sendMessage(RED  + invitedPlayerName + " already is invited to a faction.");
            return;
        }
        Invite invite = new Invite(invitedPlayer.getUniqueId(), factionId, System.currentTimeMillis() + 30000);
        simpleFactionsPlugin.invites.put(invitedPlayer.getUniqueId(), invite);

        invitedPlayer.sendMessage(GREEN + "You have been invited for the faction " + faction.name());
        invitedPlayer.sendMessage("to accept type: /faction accept");
        invitedPlayer.sendMessage("to decline type: /faction decline");

        player.sendMessage(GREEN + "Invited " + invitedPlayerName + " to your faction.");
    }

    public void join(Player player, String factionName) throws SQLException
    {
        if(simpleFactionsPlugin.playerDatabase.hasFaction(player))
        {
            player.sendMessage(RED + "You are already in a faction.");
            return;
        }
        if(!simpleFactionsPlugin.factionDatabase.factionExistsByName(factionName)){
            player.sendMessage(RED + "faction " + factionName + " does not exist.");
            return;
        }
        Faction faction = simpleFactionsPlugin.factionDatabase.getFactionDataByName(factionName);
        Player receiver = Bukkit.getPlayer(faction.owner());
        if (receiver == null){
            player.sendMessage(RED + "Something went wrong while making a join request.");
            return;
        }
        Join join = new Join(faction.owner(), player.getUniqueId(), faction.id(), System.currentTimeMillis() + 30000);
        simpleFactionsPlugin.joins.put(faction.owner(), join);

        player.sendMessage(GREEN + "You have send a join request to " + faction.name());

        receiver.sendMessage(GREEN + player.getName() + " wants to join your faction.");
        receiver.sendMessage("to accept type: /faction accept");
        receiver.sendMessage("to decline type: /faction decline");
    }

    public void accept(Player player) throws SQLException
    {
        if (simpleFactionsPlugin.invites.containsKey(player.getUniqueId()))
        {
            Invite invite = simpleFactionsPlugin.invites.get(player.getUniqueId());
            Faction faction = simpleFactionsPlugin.factionDatabase.getFactionDataById(invite.factionId());

            simpleFactionsPlugin.factionDatabase.updateFactionMembers(invite.factionId(), player.getName(), true);
            simpleFactionsPlugin.playerDatabase.updateFactionWithPlayerName(player.getName(), invite.factionId());

            changePlayerDisplayName(player, faction.color() + "[" + faction.name() + "] " + player.getName());
            player.sendMessage(GREEN + "you have joined the faction " + faction.name());

            simpleFactionsPlugin.invites.remove(player.getUniqueId());
            return;
        }
        if(simpleFactionsPlugin.joins.containsKey(player.getUniqueId())){
            Join join = simpleFactionsPlugin.joins.get(player.getUniqueId());
            String senderName = simpleFactionsPlugin.playerDatabase.getPlayerName(join.sender().toString());
            Faction faction = simpleFactionsPlugin.factionDatabase.getFactionDataById(join.factionId());

            simpleFactionsPlugin.factionDatabase.updateFactionMembers(join.factionId(), senderName, true);
            simpleFactionsPlugin.playerDatabase.updateFactionWithPlayerUUID(join.sender(), join.factionId());

            Player sender = Bukkit.getPlayer(join.sender());

            if(sender != null)
            {
                changePlayerDisplayName(sender, faction.color() + "[" + faction.name() + "] " + player.getName());
                sender.sendMessage(GREEN + "You have joined the faction " + faction.name());
            }
            player.sendMessage(GREEN + senderName + " is now part of your faction.");

            return;
        }
        player.sendMessage(RED  + "You don't have any pending invites or requests." );
    }

    public void decline(Player player) throws SQLException
    {
        if (simpleFactionsPlugin.invites.containsKey(player.getUniqueId()))
        {
            Invite invite = simpleFactionsPlugin.invites.get(player.getUniqueId());
            Faction faction = simpleFactionsPlugin.factionDatabase.getFactionDataById(invite.factionId());

            player.sendMessage(GREEN + "you have declined the faction invitation from " + faction.name());

            simpleFactionsPlugin.invites.remove(player.getUniqueId());
            return;
        }
        if(simpleFactionsPlugin.joins.containsKey(player.getUniqueId()))
        {
            Join join = simpleFactionsPlugin.joins.get(player.getUniqueId());
            String senderName = simpleFactionsPlugin.playerDatabase.getPlayerName(join.sender().toString());
            player.sendMessage(RED + "You have declined the join request from " + senderName);

            Player sender = Bukkit.getPlayer(join.sender());

            if(sender != null)
            {
                sender.sendMessage(GREEN + player.getName() + " declined your join request.");
            }

            simpleFactionsPlugin.joins.remove(player.getUniqueId());
            return;
        }
        player.sendMessage(RED  + "You don't have any pending invites or requests." );
    }

    public void modifyName(Player player, String newFactionName) throws SQLException
    {
        if (!simpleFactionsPlugin.playerDatabase.hasFaction(player)) {
            player.sendMessage(RED + "You must be in a faction to modify it.");
            return;
        }

        int factionId = simpleFactionsPlugin.playerDatabase.getFactionId(player);
        Faction faction = simpleFactionsPlugin.factionDatabase.getFactionDataById(factionId);

        if(!faction.owner().equals(player.getUniqueId())){
            player.sendMessage(RED + "Only the owner can modify a faction.");
            return;
        }
        if(simpleFactionsPlugin.factionDatabase.factionExistsByName(newFactionName)){
            player.sendMessage(RED + "There already is a faction with the name " + newFactionName + ".");
            return;
        }

        simpleFactionsPlugin.factionDatabase.updateFactionName(factionId, newFactionName);
        player.sendMessage(GREEN + "Changed faction name from " + faction.name() + " to " + newFactionName);
        faction = simpleFactionsPlugin.factionDatabase.getFactionDataById(factionId);

        for(int i = 0; i < faction.members().size(); i++)
        {
            Player member = player.getServer().getPlayer(faction.members().get(i));

            if(member != null)
            {
            changePlayerDisplayName(member, faction.color() + "[" + faction.name() + "] " + player.getName());
            }
        }
    }

    public void modifyColor(Player player, String newColor) throws SQLException
    {
        if (!simpleFactionsPlugin.playerDatabase.hasFaction(player))
        {
            player.sendMessage(RED + "You must be in a faction to modify it.");
            return;
        }
        if(!colors.colorName.contains(newColor.toLowerCase())){
            player.sendMessage(RED + "Not a valid color possible options are:");
            player.sendMessage("black, red, aqua, blue, dark_aqua");
            player.sendMessage("dark_blue, dark_gray, dark_green");
            player.sendMessage("dark_purple, dark_red, gold, gray");
            player.sendMessage("green, light_purple, white, yellow");
            return;
        }

        int factionId = simpleFactionsPlugin.playerDatabase.getFactionId(player);
        Faction faction = simpleFactionsPlugin.factionDatabase.getFactionDataById(factionId);

        if(!faction.owner().equals(player.getUniqueId()))
        {
            player.sendMessage(RED + "Only the owner can modify a faction.");
            return;
        }

        simpleFactionsPlugin.factionDatabase.updateFactionColor(factionId, newColor);
        player.sendMessage(GREEN + "Changed faction color from " + faction.color() + colors.getColorNameWithChatColor(faction.color())
                + GREEN + " to " + colors.getChatColorWithColorName(newColor) + newColor);
        faction = simpleFactionsPlugin.factionDatabase.getFactionDataById(factionId);

        for(int i = 0; i < faction.members().size(); i++)
        {
            Player member = player.getServer().getPlayer(faction.members().get(i));

            if(member != null)
            {
                changePlayerDisplayName(member, faction.color() + "[" + faction.name() + "] " + player.getName());
            }
        }
    }

    public void modifyOwner(Player player, String newOwnerName) throws SQLException
    {
        if (!simpleFactionsPlugin.playerDatabase.hasFaction(player))
        {
            player.sendMessage(RED + "You must be in a faction to modify it.");
            return;
        }

        int factionId = simpleFactionsPlugin.playerDatabase.getFactionId(player);
        Faction faction = simpleFactionsPlugin.factionDatabase.getFactionDataById(factionId);

        if(!faction.owner().equals(player.getUniqueId()))
        {
            player.sendMessage(RED + "Only the owner can modify a faction.");
            return;
        }
        if(!faction.members().contains(newOwnerName)){
            player.sendMessage(RED + "Player must be in your faction to make him the owner.");
            return;
        }

        String newOwnerUUID = simpleFactionsPlugin.playerDatabase.getPlayerUUID(newOwnerName).toString();
        simpleFactionsPlugin.factionDatabase.updateFactionOwner(factionId, newOwnerUUID);
        player.sendMessage(GREEN + "Change the owner of " + faction.name() + " to " + newOwnerName);
    }

    private void changePlayerDisplayName(Player player, String newDisplayName)
    {
        player.setDisplayName(newDisplayName);
        player.setPlayerListName(newDisplayName);
        player.setCustomName(newDisplayName);
    }
}