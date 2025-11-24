package com.gatto.sector.service;

import com.gatto.sector.entity.Sector;
import com.gatto.sector.entity.UserSectorSelection;
import com.gatto.sector.error.SectorDoesNotExistException;
import com.gatto.sector.repository.SectorRepository;
import com.gatto.sector.repository.UserSectorSelectionRepository;
import com.gatto.sector.view.UserSelectionView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSelectionService {

    private final UserSectorSelectionRepository userSelectionRepo;
    private final SectorRepository sectorRepository;

    @Transactional
    public UserSelectionView saveSelection(UserSelectionView view) {
        String username = view.username();

        userSelectionRepo.deleteByUsername(username);

        for (Long sectorId : view.sectorIds()) {
            Sector sector = sectorRepository.findById(sectorId)
                    .orElseThrow(SectorDoesNotExistException::new);

            UserSectorSelection row = new UserSectorSelection();
            row.setUsername(username);
            row.setSector(sector);
            userSelectionRepo.save(row);
        }

        return getSelection(username);
    }

    @Transactional(readOnly = true)
    public UserSelectionView getSelection(String username) {
        List<UserSectorSelection> rows = userSelectionRepo.findByUsername(username);

        List<Long> sectorIds = rows.stream()
                .map(r -> r.getSector().getId())
                .toList();

        return new UserSelectionView(username, sectorIds);
    }
}
