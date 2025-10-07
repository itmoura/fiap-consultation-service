package com.fiap.itmoura.consultation_service.shared.application.impl;

import com.fiap.itmoura.consultation_service.shared.domain.UserDetailData;
import com.fiap.itmoura.consultation_service.user.infrastructure.persistence.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userOptional = userRepository.findByEmail(username);
        var user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User [" + username + "] not found"));

        return new UserDetailData(user);
    }
}
