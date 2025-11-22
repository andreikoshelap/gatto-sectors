package com.gatto.sector.service;

import com.gatto.sector.repository.SectorRepository;
import com.gatto.sector.view.SectorView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SectorService {
    private final SectorRepository repository;


    public List<SectorView> getAllSectors() {
        return repository.findAll().stream()
                .map(sector -> new SectorView(
                        sector.getId(),
                        sector.getName(),
                        sector.getParent() != null ? sector.getParent().getId() : null
                ))
                .toList();
    }

    public Optional<SectorView> findById(Long id) {
        return repository.findById(id).map(sector -> new SectorView(
                sector.getId(),
                sector.getName(),
                sector.getParent() != null ? sector.getParent().getId() : null
        ));
    }
}
