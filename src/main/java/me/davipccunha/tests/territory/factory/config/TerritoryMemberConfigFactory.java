package me.davipccunha.tests.territory.factory.config;

import me.davipccunha.tests.territory.model.TerritoryMemberConfig;

public class TerritoryMemberConfigFactory {
    public static TerritoryMemberConfig createDefault() {
        return new TerritoryMemberConfig(true, true, true);
    }
}
