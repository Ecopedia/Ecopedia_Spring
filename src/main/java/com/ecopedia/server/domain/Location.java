package com.ecopedia.server.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String gu;

    private String dong;

    @OneToMany(mappedBy = "location")
    List<Creature> creatures = new ArrayList<>();

}
