package com.example.springredditclone.service;

import com.example.springredditclone.model.User;
import com.example.springredditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.singletonList;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(userName);

//        if (userOptional.isEmpty()) {
//            throw new EntityNotFoundException("No user found with username: " + userName);
//        }

        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + userName));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.isEnabled(),
                true, true, true, getAuthorities("User"));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return singletonList(new SimpleGrantedAuthority(role));
    }

    /*
    * The class overrides the method loadUserByUsername() which is used by Spring Security to fetch the user details.
    * Inside the method, we are querying the UserRepository and fetching those details and wrapping them in another
    * User object which implements the UserDetails interface.
    */
}
