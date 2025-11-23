package com.gatto.sector.service;

import com.gatto.sector.entity.Sector;
import com.gatto.sector.error.SectorDoesNotExistException;
import com.gatto.sector.repository.SectorRepository;
import com.gatto.sector.view.SectorView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
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

    @Transactional
    public void deleteById(Long id) {
        Sector sector = repository.findById(id)
                .orElseThrow(SectorDoesNotExistException::new);
        repository.delete(sector);
        log.debug("Deleted resource id={}", id);
    }

    @Transactional
    public SectorView create(SectorView view) {
        Sector entity = new Sector();
        entity.setName(view.name());

        if (view.parentId() != null) {
            Sector parent = repository.findById(view.parentId())
                    .orElseThrow(SectorDoesNotExistException::new);
            entity.setParent(parent);
        }

        Sector saved = repository.save(entity);
        log.debug("Created sector id={} name={}", saved.getId(), saved.getName());

        return new SectorView(
                saved.getId(),
                saved.getName(),
                saved.getParent() != null ? saved.getParent().getId() : null
        );
    }

    @Transactional
    public SectorView update(Long id, SectorView resourceView) {
        if (resourceView.id() == null || !resourceView.id().equals(id)) {
            throw new SectorDoesNotExistException();
        }

        Sector existing = repository.findById(id)
                .orElseThrow(SectorDoesNotExistException::new);
        Sector saved = repository.save(existing);

        log.debug("Updated sector id={} name={} ", saved.getId(), saved.getName());
        return SectorView.fromEntity(saved);
    }

}
