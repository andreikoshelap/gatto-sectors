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
            SectorView resource = sectorService.findById(id).orElseThrow();
            return ResponseEntity.ok(resource);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while retrieving resource with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SectorView> update(@PathVariable Long id, @RequestBody SectorView view) {
        try {
            if (view.id() != null && !view.id().equals(id)) {
                log.warn("Path ID {} != payload ID {}", id, view.id());
            }
            SectorView savedResource = sectorService.update(id, view);
            return ResponseEntity.ok(savedResource);
        } catch (NoSuchElementException e) {
            log.warn("Resource not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while updating resource with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            log.info("Deleting resource with ID: {}", id);
            sectorService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            log.warn("Resource not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while deleting resource with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

}
