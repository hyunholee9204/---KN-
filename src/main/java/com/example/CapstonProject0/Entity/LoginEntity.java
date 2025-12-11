package com.example.CapstonProject0.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity @Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Slf4j @ToString
@Table(name = "login_list")
public class LoginEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String name;
}
