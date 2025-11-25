package com.gatto.sector.repository;

import com.gatto.sector.entity.Sector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SectorRepositoryTest {

    @Autowired
    private SectorRepository sectorRepository;

    @Test
    void saveAndLoadSectorWithParent() {
        // given: root sector
        Sector root = new Sector();
        root.setName("Root test sector");
        Sector savedRoot = sectorRepository.save(root);

        // and: child sector with parent
        Sector child = new Sector();
        child.setName("Child test sector");
        child.setParent(savedRoot);
        Sector savedChild = sectorRepository.save(child);

        // when: loading back from DB
        Optional<Sector> foundOpt = sectorRepository.findById(savedChild.getId());

        // then
        assertThat(foundOpt).isPresent();
        Sector found = foundOpt.get();

        assertThat(found.getName()).isEqualTo("Child test sector");
        assertThat(found.getParent()).isNotNull();
        assertThat(found.getParent().getId()).isEqualTo(savedRoot.getId());
        assertThat(found.getParent().getName()).isEqualTo("Root test sector");
    }
}
