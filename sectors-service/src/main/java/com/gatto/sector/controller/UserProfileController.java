package com.gatto.sector.controller;

import com.gatto.sector.service.UserProfileService;
import com.gatto.sector.view.UserProfile;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class UserProfileController {

    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @PostMapping("/profile")
    public UserProfile save(@Valid @RequestBody UserProfile dto) {
        return service.saveProfile(dto);
    }

    @GetMapping("/profile")
    public UserProfile get(@RequestParam String name) {
        return service.getProfile(name);
    }
}
