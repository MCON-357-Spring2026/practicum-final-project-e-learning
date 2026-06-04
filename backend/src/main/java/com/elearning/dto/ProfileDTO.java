package com.elearning.dto;

import com.elearning.enums.Role;
import com.elearning.model.Person;
import com.elearning.model.User;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class ProfileDTO extends Person{
    private String username;
    private String password;
    private String email;
    private Role role;

    public ProfileDTO(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.setId(user.getId());
        this.setFirstName(user.getFirstName());
        this.setLastName(user.getLastName());
        this.setAddress(user.getAddress());
        this.setGender(user.getGender());
        this.setDateOfBirth(user.getDateOfBirth());
    }
}
