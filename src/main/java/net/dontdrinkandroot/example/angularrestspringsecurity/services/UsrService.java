package net.dontdrinkandroot.example.angularrestspringsecurity.services;


import net.dontdrinkandroot.example.angularrestspringsecurity.entity.User;
import net.dontdrinkandroot.example.angularrestspringsecurity.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class UsrService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByName(username);
    }

    @Transactional
    public User save(User u) throws UsernameNotFoundException {
        return userRepository.save(u);
    }
}
