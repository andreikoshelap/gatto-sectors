package com.gatto.sector.view;

import java.util.List;

public record UserSelectionView(
        String username,
        List<Long>sectorIds
) {}
