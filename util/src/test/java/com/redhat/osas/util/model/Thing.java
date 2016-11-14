package com.redhat.osas.util.model;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Thing {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    Integer id;
    @Getter
    @Setter
    @Column
    String name;
}
