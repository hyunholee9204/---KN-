package com.example.CapstonProject0.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupForm {
    private String username;
    private String password;
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
