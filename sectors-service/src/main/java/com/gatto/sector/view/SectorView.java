package com.gatto.sector.view;

import com.gatto.sector.entity.Sector;

public record SectorView(
        Long id,
        String name,
        Long parentId
) {

    public static SectorView fromEntity(Sector s) {
        return new SectorView(
                s.getId(),
                s.getName(),
                s.getParent() != null ? s.getParent().getId() : null
        );
    }
}
