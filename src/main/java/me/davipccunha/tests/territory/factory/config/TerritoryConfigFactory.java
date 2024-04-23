package me.davipccunha.tests.territory.factory.config;

import me.davipccunha.tests.territory.model.TerritoryConfig;
import me.davipccunha.tests.territory.model.TerritoryMemberConfig;

import java.util.HashMap;
import java.util.HashSet;

public class TerritoryConfigFactory {
    public static TerritoryConfig createDefault(String owner) {
        HashMap<String, TerritoryMemberConfig> defaultMembers = new HashMap<>(){{
            put(owner, new TerritoryMemberConfig(true, true, true));
        }};

        return new TerritoryConfig(true, false, defaultMembers, new HashSet<>());
    }
}
