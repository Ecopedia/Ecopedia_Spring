package com.ecopedia.server.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatureImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String imageUrl;

    private String accessUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private Creature creature;


}
