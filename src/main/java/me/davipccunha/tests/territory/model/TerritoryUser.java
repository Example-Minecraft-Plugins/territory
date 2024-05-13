package me.davipccunha.tests.territory.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public class TerritoryUser {
    private final String username;
    private final Set<TerritoryBlock> territories = new HashSet<>();

    public void addTerritory(TerritoryBlock center) {
        this.territories.add(center);
    }

    public void removeTerritory(TerritoryBlock center) {
        this.territories.remove(center);
    }

    public int getTerritoriesAmount() {
        return this.territories.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TerritoryUser other = (TerritoryUser) obj;
        return username.equals(other.username);
    }

    @Override
    public int hashCode() {
        return this.username.hashCode();
    }
}
