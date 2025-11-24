package com.gatto.sector.controller;

import com.gatto.sector.service.UserSelectionService;
import com.gatto.sector.view.UserSelectionView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-selections")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class UserSelectionController {

    private final UserSelectionService userSelectionService;

    @PostMapping
    public ResponseEntity<UserSelectionView> save(@RequestBody UserSelectionView view) {
        return ResponseEntity.ok(userSelectionService.saveSelection(view));
    }

    @GetMapping
    public ResponseEntity<UserSelectionView> get(@RequestParam String username) {
        UserSelectionView view = userSelectionService.getSelection(username);
        return ResponseEntity.ok(view);
    }
}
