package me.davipccunha.tests.territory.model;

import lombok.AllArgsConstructor;
import lombok.Setter;

// Not using @Getter because of Lombok boolean getters naming and not being able to set fluent=true only for Getters
@Setter
@AllArgsConstructor
public class TerritoryMemberConfig {
    private boolean canBuild;
    private boolean canInteract;
    private boolean canDamageEntities;

    public boolean canBuild() {
        return canBuild;
    }

    public boolean canInteract() {
        return canInteract;
    }

    public boolean canDamageEntities() {
        return canDamageEntities;
    }
}
