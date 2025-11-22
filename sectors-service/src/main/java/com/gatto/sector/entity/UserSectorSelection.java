package com.gatto.sector.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "user_sector_selection",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"username", "sector_id"})
        }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSectorSelection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sector_id")
    private Sector sector;

}
