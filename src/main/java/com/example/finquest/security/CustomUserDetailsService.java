package com.example.finquest.security;

import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.ParentUserEntity;
import com.example.finquest.repository.ChildUserRepository;
import com.example.finquest.repository.ParentUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ParentUserRepository parentUserRepository;

    @Autowired
    private ChildUserRepository childUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ParentUserEntity> parentUser = parentUserRepository.findByUsername(username);
        if (parentUser.isPresent()) {
            ParentUserEntity user = parentUser.get();
            return new CustomUserDetails(user.getId(), user.getUsername(), user.getPassword(), user.getRoles(), true // Assuming all users are enabled by default
            );
        
        }

        // 2. Try to load ChildUser
        ChildUserEntity childUser = childUserRepository.findByName(username);
        if (childUser != null) {
            return new CustomUserDetails(childUser.getId(), childUser.getName(), childUser.getPassword(), childUser.getRoles(), true // Assuming all users are enabled by default
            );
        }

        // 3. If neither parent nor child is found, throw an exception
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
