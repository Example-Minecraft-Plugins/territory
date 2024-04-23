package me.davipccunha.tests.territory.model;

import lombok.*;
import me.davipccunha.tests.territory.factory.config.TerritoryMemberConfigFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TerritoryConfig {
    private boolean accessible;
    private boolean pvpEnabled;

    @Getter(AccessLevel.NONE)
    private final HashMap<String, TerritoryMemberConfig> members;

    @Getter(AccessLevel.NONE)
    // TODO: Implement banned players -> Cannot enter a territory even if it's public
    private final HashSet<String> bannedPlayers;

    public void addMember(String name) {
        this.bannedPlayers.remove(name);
        this.members.put(name, TerritoryMemberConfigFactory.createDefault());
    }

    public void removeMember(String name) {
        this.members.remove(name);
    }

    public TerritoryMemberConfig getMemberConfig(String name) {
        return this.members.get(name);
    }

    public int getMembersCount() {
        return this.members.size();
    }

    public boolean hasMember(String name) {
        return this.members.containsKey(name);
    }

    public Set<String> getMembers() {
        return this.members.keySet();
    }

    // TODO: When a player is banned, check if he is inside the territory area and remove him
    public void banPlayer(String name) {
        this.removeMember(name);
        this.bannedPlayers.add(name);
    }

    public void unbanPlayer(String name) {
        this.bannedPlayers.remove(name);
    }

    public boolean isBanned(String name) {
        return this.bannedPlayers.contains(name);
    }

    public Set<String> getBannedPlayers() {
        return this.bannedPlayers;
    }
}
