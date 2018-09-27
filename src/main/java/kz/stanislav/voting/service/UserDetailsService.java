package kz.stanislav.voting.service;

import kz.stanislav.voting.persistence.model.User;
import kz.stanislav.voting.persistence.dao.UserRepository;
import kz.stanislav.voting.web.AuthorizedUser;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository repository;

    @Autowired
    public UserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = repository.findByEmail(email.trim().toLowerCase());

        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User " + email + " is not found");
        }

        return new AuthorizedUser(user.get());
    }
}