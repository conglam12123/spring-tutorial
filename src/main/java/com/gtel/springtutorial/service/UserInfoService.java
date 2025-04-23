package com.gtel.springtutorial.service;

import com.gtel.springtutorial.model.entity.UserEntity;
import com.gtel.springtutorial.model.securities.UserInfoDetails;
import com.gtel.springtutorial.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserInfoService implements UserDetailsService {

    final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntityOptional = userRepo.findByPhoneNumber(username);

        if (userEntityOptional.isEmpty()) {
            throw new UsernameNotFoundException("user" + username + "not found");

        }
        return new UserInfoDetails(userEntityOptional.get());
    }
}
