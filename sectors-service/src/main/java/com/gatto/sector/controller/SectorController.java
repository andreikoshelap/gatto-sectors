package com.gatto.sector.controller;

import com.gatto.sector.service.SectorService;
import com.gatto.sector.view.SectorView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/sectors")
@CrossOrigin
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
//            log.warn("Resource not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (ObjectOptimisticLockingFailureException e) {
//            log.error("Optimistic locking failure while retrieving resource with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
