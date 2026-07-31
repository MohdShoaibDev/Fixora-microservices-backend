package com.shoaib.authservice.security;

import com.shoaib.authservice.entity.User;
import com.shoaib.authservice.repository.UserRepository;
import com.shoaib.authservice.utility.UserStatus;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndIsActive(username, UserStatus.ACTIVE)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        return new CustomUserDetails(user);
    }
}
