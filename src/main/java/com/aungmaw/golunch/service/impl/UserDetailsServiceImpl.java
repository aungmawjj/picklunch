package com.aungmaw.golunch.service.impl;

import com.aungmaw.golunch.model.UserEntity;
import com.aungmaw.golunch.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @PostConstruct
    private void loadUsers() {
        List<UserEntity> users = List.of(
                UserEntity.builder()
                        .username("user1")
                        .encodedPassword("$2y$05$akKpMk4WXh2VYDTwZikOQO5rmFpQjBMgQHX2jPOcM5wK./BmOrPnu")
                        .build(),
                UserEntity.builder()
                        .username("user2")
                        .encodedPassword("$2y$05$ZjbL5OExkYSGw0uvMTt3v.KmL7US0Cg5iYzm3NsEeY/9cwS5Kg4jK")
                        .build()
        );
        userRepository.saveAll(users);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) return null;

        return User.builder()
                .username(username)
                .password(userEntity.getEncodedPassword())
                .build();
    }

}
