package com.example.demo.entity;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "role")
public class RoleEntity implements GrantedAuthority {
    @Id
    private String authority;

    public RoleEntity() {
    }

    @Override
    public String getAuthority() {
        // permet à Spring Security de connaître le nom correspondant au role
        return this.authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}