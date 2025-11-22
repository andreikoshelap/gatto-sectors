package com.gatto.sector.service;

import com.gatto.sector.entity.Sector;
import com.gatto.sector.entity.UserSectorSelection;
import com.gatto.sector.repository.SectorRepository;
import com.gatto.sector.repository.UserSectorSelectionRepository;
import com.gatto.sector.view.UserProfile;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserProfileService {

    private final UserSectorSelectionRepository selectionRepo;
    private final SectorRepository sectorRepo;

    public UserProfileService(UserSectorSelectionRepository selectionRepo,
                              SectorRepository sectorRepo) {
        this.selectionRepo = selectionRepo;
        this.sectorRepo = sectorRepo;
    }

    @Transactional
    public UserProfile saveProfile(UserProfile view) {
        selectionRepo.deleteByUsername(view.name());

        List<Sector> sectors = sectorRepo.findAllById(view.sectorIds());

        if (sectors.size() != view.sectorIds().size()) {
            throw new IllegalArgumentException("Some sectors not found");
        }

        for (Sector sector : sectors) {
            UserSectorSelection s = new UserSectorSelection();
            s.setUsername(view.name());
            s.setSector(sector);
            selectionRepo.save(s);
        }

        return view;
    }

    @Transactional(readOnly = true)
    public UserProfile getProfile(String name) {
        List<UserSectorSelection> selections = selectionRepo.findByUsername(name);
        if (selections.isEmpty()) {
            throw new EntityNotFoundException("Profile not found");
        }
        return new UserProfile(name, selections.stream()
                .map(s -> s.getSector().getId())
                .toList());
    }
}
