package com.example.demo.auth.service;

import com.example.demo.auth.model.User;
import com.example.demo.auth.repository.UserRepository;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
public class UserDetailsServiceImpl  implements UserDetailsService{
    private final UserRepository repository;

    public UserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
        User currentUser = repository.findByEmail(email);
        return new org.springframework.security.core
                .userdetails.User(email, currentUser.getPassword(), true, true, true, true, AuthorityUtils.createAuthorityList("USER"));
    }


}