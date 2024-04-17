package me.entropire.simplefactions.objects;

import java.util.UUID;

public record Join(UUID receiver, UUID sender, int factionId, long expireDate)
{

}