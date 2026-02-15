package com.project.traffic_enforcement.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.traffic_enforcement.models.Users;
import com.project.traffic_enforcement.repository.UsersRepository;
import com.project.traffic_enforcement.security.config.UserDetails;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private  UsersRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final Users user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found -> "+email));
        UserDetails userDetails = UserDetails.build(user);
        return userDetails;
    }
}
