package com.gatto.sector.view;

import java.util.List;

public record UserProfile(
        String name,
        List<Long>sectorIds
) {}
