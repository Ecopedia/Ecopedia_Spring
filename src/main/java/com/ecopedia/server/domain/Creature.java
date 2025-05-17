package com.ecopedia.server.domain;


import com.ecopedia.server.domain.enums.CreatureCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Creature {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false)
    private String creatureName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String creatureExplain;

    @Column(nullable = false)
    private Double latitude; // 위도

    @Column(nullable = false)
    private Double longitude; // 경도

    @Enumerated(EnumType.STRING)
    private CreatureCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_idx")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_idx")
    private Location location;


}

