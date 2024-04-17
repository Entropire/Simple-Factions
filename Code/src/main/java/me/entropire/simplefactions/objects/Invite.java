package me.entropire.simplefactions.objects;

import java.util.UUID;

public record Invite(UUID playerUUID, int factionId, long expireDate)
{

}
