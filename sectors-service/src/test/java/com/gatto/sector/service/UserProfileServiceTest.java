package com.gatto.sector.service;

import com.gatto.sector.entity.Sector;
import com.gatto.sector.entity.UserSectorSelection;
import com.gatto.sector.repository.SectorRepository;
import com.gatto.sector.repository.UserSectorSelectionRepository;
import com.gatto.sector.view.UserProfile;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserSectorSelectionRepository selectionRepo;

    @Mock
    private SectorRepository sectorRepo;

    @InjectMocks
    private UserProfileService service;

    @Test
    @DisplayName("saveProfile: deletes old selections and saves new ones for all found sectors")
    void saveProfile_savesSelections() {
        String username = "john";
        List<Long> sectorIds = List.of(1L, 2L);

        Sector s1 = new Sector();
        s1.setId(1L);
        Sector s2 = new Sector();
        s2.setId(2L);

        // all sectors found
        when(sectorRepo.findAllById(sectorIds)).thenReturn(List.of(s1, s2));

        UserProfile input = new UserProfile(username, sectorIds);

        UserProfile result = service.saveProfile(input);

        // 1) deleted old records
        verify(selectionRepo).deleteByUsername(username);

        // 2) saved new records, one per sector
        ArgumentCaptor<UserSectorSelection> captor =
                ArgumentCaptor.forClass(UserSectorSelection.class);
        verify(selectionRepo, times(2)).save(captor.capture());

        List<UserSectorSelection> savedSelections = captor.getAllValues();
        assertEquals(2, savedSelections.size());

        // verify that username and sector ids match
        assertEquals(username, savedSelections.get(0).getUsername());
        assertEquals(username, savedSelections.get(1).getUsername());

        List<Long> savedSectorIds = savedSelections.stream()
                .map(sel -> sel.getSector().getId())
                .toList();
        assertTrue(savedSectorIds.containsAll(sectorIds));

        // 3) method returns the same profile it received
        assertEquals(username, result.name());
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
        when(sectorRepo.findAllById(sectorIds)).thenReturn(List.of(s1));

        UserProfile input = new UserProfile(username, sectorIds);

        assertThrows(IllegalArgumentException.class,
                () -> service.saveProfile(input));

        // save should not be called
        verify(selectionRepo, never()).save(any());
    }

    @Test
    @DisplayName("getProfile: returns UserProfile with name and list of sector ids")
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

        UserProfile result = service.getProfile(username);

        assertEquals(username, result.name());
        assertEquals(List.of(10L, 20L), result.sectorIds());
    }

    @Test
    @DisplayName("getProfile: throws EntityNotFoundException if there are no selections")
    void getProfile_throwsWhenNoSelections() {
        String username = "nobody";

        when(selectionRepo.findByUsername(username))
                .thenReturn(List.of());

        assertThrows(EntityNotFoundException.class,
                () -> service.getProfile(username));
    }
}
