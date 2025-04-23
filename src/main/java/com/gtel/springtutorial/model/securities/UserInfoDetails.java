package com.gtel.springtutorial.model.securities;

import com.gtel.springtutorial.model.entity.UserEntity;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class UserInfoDetails implements UserDetails {
    private String userName;
    private String password;
    private List<GrantedAuthority> authorities;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    public UserInfoDetails () {

    }

    public UserInfoDetails(UserEntity userEntity) {
        this.password = userEntity.getPassword();
        this.userName = userEntity.getPhoneNumber();
        this.authorities = List.of();

    }
}
