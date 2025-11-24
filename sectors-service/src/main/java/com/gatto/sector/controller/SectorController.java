package com.gatto.sector.controller;

import com.gatto.sector.service.SectorService;
import com.gatto.sector.view.SectorView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/sectors")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RequiredArgsConstructor
public class SectorController {

    private final SectorService sectorService;

    @GetMapping
    public List<SectorView> all() {
        return sectorService.getAllSectors();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectorView> get(@PathVariable Long id) {
        try {
            SectorView sector = sectorService.findById(id).orElseThrow();
            return ResponseEntity.ok(sector);
        } catch (NoSuchElementException e) {
            log.warn("Sector not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while retrieving sector with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SectorView> update(@PathVariable Long id, @RequestBody SectorView view) {
        try {
            if (view.id() != null && !view.id().equals(id)) {
                log.warn("Path ID {} != payload ID {}", id, view.id());
            }
            SectorView savedSector = sectorService.update(id, view);
            return ResponseEntity.ok(savedSector);
        } catch (NoSuchElementException e) {
            log.warn("Sector not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while updating sector with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            log.info("Deleting sector with ID: {}", id);
            sectorService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Sector not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while deleting sector with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping()
    public ResponseEntity<SectorView> create(@RequestBody SectorView view) {
        try {
            SectorView savedSector = sectorService.create(view);
            return ResponseEntity.ok(savedSector);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while create sector", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

}
