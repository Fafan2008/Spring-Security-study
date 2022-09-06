package com.ivo.my.models.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Collection<Role> roles;
}
