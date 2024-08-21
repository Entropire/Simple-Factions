package me.entropire.simple_factions.objects;

import java.util.UUID;

public record Invite(UUID playerUUID, int factionId, long expireDate)
{

}
