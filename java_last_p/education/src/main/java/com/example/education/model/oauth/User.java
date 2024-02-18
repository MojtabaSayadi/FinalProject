package com.example.education.model.oauth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_sec")
    @SequenceGenerator(name = "users_sec", sequenceName = "users_sec", allocationSize = 1)
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String email;
    private String startTime;
    private String endTime;
    private Integer active;
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> authorities = new HashSet<>();

    public User() {
    }

    public User(String username, String email, String password, Integer active, String startTime, String endTime) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.active = active;
        this.startTime = startTime;
        this.endTime = endTime;
    }


}
