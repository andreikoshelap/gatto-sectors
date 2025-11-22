package com.gatto.sector.repository;

import com.gatto.sector.entity.UserSectorSelection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSectorSelectionRepository extends JpaRepository<UserSectorSelection, Long> {

    List<UserSectorSelection> findByUsername(String username);

    void deleteByUsername(String username);
}
