package com.gatto.sector.repository;

import com.gatto.sector.entity.Sector;
import com.gatto.sector.entity.UserSectorSelection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserSectorSelectionRepositoryTest {

    @Autowired
    private UserSectorSelectionRepository userSectorSelectionRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @Test
    void saveAndFindByUsername() {
        // given
        Sector sector1 = new Sector();
        sector1.setName("Test sector 1");
        Sector savedSector1 = sectorRepository.save(sector1);

        Sector sector2 = new Sector();
        sector2.setName("Test sector 2");
        Sector savedSector2 = sectorRepository.save(sector2);

        UserSectorSelection row1 = new UserSectorSelection();
        row1.setUsername("john");
        row1.setSector(savedSector1);

        UserSectorSelection row2 = new UserSectorSelection();
        row2.setUsername("john");
        row2.setSector(savedSector2);

        userSectorSelectionRepository.save(row1);
        userSectorSelectionRepository.save(row2);

        // when
        List<UserSectorSelection> selections = userSectorSelectionRepository.findByUsername("john");

        // then
        assertThat(selections).hasSize(2);
        assertThat(selections)
                .extracting(s -> s.getSector().getName())
                .containsExactlyInAnyOrder("Test sector 1", "Test sector 2");

    }

    @Test
    void deleteByUsername_removesAllRowsForUser() {
        // given
        Sector sector1 = new Sector();
        sector1.setName("Test sector 1");
        Sector savedSector1 = sectorRepository.save(sector1);

        Sector sector2 = new Sector();
        sector2.setName("Test sector 2");
        Sector savedSector2 = sectorRepository.save(sector2);

        UserSectorSelection row1 = new UserSectorSelection();
        row1.setUsername("anna");
        row1.setSector(savedSector1);

        UserSectorSelection row2 = new UserSectorSelection();
        row2.setUsername("anna");
        row2.setSector(savedSector2);

        userSectorSelectionRepository.save(row1);
        userSectorSelectionRepository.save(row2);

        // when
        userSectorSelectionRepository.deleteByUsername("anna");

        // then
        assertThat(userSectorSelectionRepository.findByUsername("anna")).isEmpty();
    }

}
