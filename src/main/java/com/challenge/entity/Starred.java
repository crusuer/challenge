package com.challenge.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
public class Starred {
    @Id
    private Long id;
    @Column
    private String name;
    @Column
    private String htmlUrl;
    @Column
    private String description;
    @Column
    private String language;
    @JsonIgnore
    private String username;
    @Column
    @ElementCollection(targetClass = String.class)
    private Set<String> tags;

}
