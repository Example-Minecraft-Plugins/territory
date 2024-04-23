package me.davipccunha.tests.territory.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TerritoryInput {
    private final String action;
    private final TerritoryBlock center;
}
