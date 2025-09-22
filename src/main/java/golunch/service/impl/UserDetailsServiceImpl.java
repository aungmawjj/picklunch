package golunch.service.impl;

import golunch.model.entity.User;
import golunch.repository.UserRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepo userRepo;

    @PostConstruct
    private void loadUsers() {
        List<Pair<String, String>> users = List.of(
                Pair.of("user1", "$2y$05$akKpMk4WXh2VYDTwZikOQO5rmFpQjBMgQHX2jPOcM5wK./BmOrPnu"),
                Pair.of("user2", "$2y$05$ZjbL5OExkYSGw0uvMTt3v.KmL7US0Cg5iYzm3NsEeY/9cwS5Kg4jK"),
                Pair.of("user3", "$2y$05$ZjbL5OExkYSGw0uvMTt3v.KmL7US0Cg5iYzm3NsEeY/9cwS5Kg4jK"),
                Pair.of("user4", "$2y$05$ZjbL5OExkYSGw0uvMTt3v.KmL7US0Cg5iYzm3NsEeY/9cwS5Kg4jK"),
                Pair.of("user5", "$2y$05$ZjbL5OExkYSGw0uvMTt3v.KmL7US0Cg5iYzm3NsEeY/9cwS5Kg4jK")
        );
        List<User> userEntities = users.stream()
                .map(user -> User.builder()
                        .username(user.getFirst())
                        .encodedPassword(user.getSecond())
                        .build())
                .toList();
        userRepo.saveAll(userEntities);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found, username=" + username);
        }
        return org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(user.getEncodedPassword())
                .build();
    }

}
