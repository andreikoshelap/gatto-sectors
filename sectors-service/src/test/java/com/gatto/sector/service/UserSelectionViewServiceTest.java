package com.gatto.sector.service;

import com.gatto.sector.entity.Sector;
import com.gatto.sector.entity.UserSectorSelection;
import com.gatto.sector.error.SectorDoesNotExistException;
import com.gatto.sector.repository.SectorRepository;
import com.gatto.sector.repository.UserSectorSelectionRepository;
import com.gatto.sector.view.UserSelectionView;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSelectionViewServiceTest {

    @Mock
    private UserSectorSelectionRepository selectionRepo;

    @Mock
    private SectorRepository sectorRepo;

    @InjectMocks
    private UserSelectionService service;

    @Test
    void saveProfile_savesSelections() {
        String username = "john";
        List<Long> sectorIds = List.of(1L, 2L);

        Sector s1 = new Sector();
        s1.setId(1L);
        Sector s2 = new Sector();
        s2.setId(2L);

        when(sectorRepo.findById(1L)).thenReturn(Optional.of(s1));
        when(sectorRepo.findById(2L)).thenReturn(Optional.of(s2));

        UserSelectionView input = new UserSelectionView(username, sectorIds);

        UserSectorSelection row1 = new UserSectorSelection();
        row1.setUsername(username);
        row1.setSector(s1);

        UserSectorSelection row2 = new UserSectorSelection();
        row2.setUsername(username);
        row2.setSector(s2);

        when(selectionRepo.findByUsername(username))
                .thenReturn(List.of(row1, row2));

        // when
        UserSelectionView result = service.saveSelection(input);

        verify(selectionRepo).deleteByUsername(username);

        ArgumentCaptor<UserSectorSelection> captor =
                ArgumentCaptor.forClass(UserSectorSelection.class);
        verify(selectionRepo, times(2)).save(captor.capture());

        List<UserSectorSelection> savedSelections = captor.getAllValues();
        assertEquals(2, savedSelections.size());

        assertEquals(username, savedSelections.get(0).getUsername());
        assertEquals(username, savedSelections.get(1).getUsername());

        List<Long> savedSectorIds = savedSelections.stream()
                .map(sel -> sel.getSector().getId())
                .toList();
        assertTrue(savedSectorIds.containsAll(sectorIds));

        assertEquals(username, result.username());
        assertEquals(sectorIds, result.sectorIds());
    }

    @Test
    @DisplayName("saveProfile: throws IllegalArgumentException if some sectors are not found")
    void saveProfile_throwsWhenSomeSectorsMissing() {
        String username = "john";
        List<Long> sectorIds = List.of(1L, 2L);

        // only one sector found instead of two
        Sector s1 = new Sector();
        s1.setId(1L);
        when(sectorRepo.findById(1L)).thenReturn(Optional.of(s1));
        when(sectorRepo.findById(2L)).thenReturn(Optional.empty());

        UserSelectionView input = new UserSelectionView(username, sectorIds);

        assertThrows(SectorDoesNotExistException.class,
                () -> service.saveSelection(input));

        // save called only once for the first sector
        verify(selectionRepo).save(any());
    }

    @Test
    @DisplayName("getProfile: returns UserSelectionView with name and list of sector ids")
    void getProfile_returnsProfile() {
        String username = "alice";

        Sector s1 = new Sector();
        s1.setId(10L);
        Sector s2 = new Sector();
        s2.setId(20L);

        UserSectorSelection sel1 = new UserSectorSelection();
        sel1.setUsername(username);
        sel1.setSector(s1);

        UserSectorSelection sel2 = new UserSectorSelection();
        sel2.setUsername(username);
        sel2.setSector(s2);

        when(selectionRepo.findByUsername(username))
                .thenReturn(List.of(sel1, sel2));

        UserSelectionView result = service.getSelection(username);

        assertEquals(username, result.username());
        assertEquals(List.of(10L, 20L), result.sectorIds());
    }

    @Test
    @DisplayName("getProfile: throws EntityNotFoundException if there are no selections")
    void getProfile_throwsWhenNoSelections() {
        String username = "nobody";

        when(selectionRepo.findByUsername(username))
                .thenReturn(List.of());

        assertThrows(EntityNotFoundException.class,
                () -> service.getSelection(username));
    }
}
