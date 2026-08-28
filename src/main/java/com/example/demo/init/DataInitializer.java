package com.example.demo.init;

import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.User;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer (UserRepository userRepository, RoleRepository roleRepository, BookRepository bookRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bookRepository = bookRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
    RoleEntity roleUser = new RoleEntity();
        roleUser.setAuthority("ROLE_USER");
        roleRepository.save(roleUser);

    RoleEntity roleAdmin = new RoleEntity();
        roleAdmin.setAuthority("ROLE_ADMIN");
        roleRepository.save(roleAdmin);

    User admin = new User();
        admin.setUsername("admin@example.com");
        admin.setPassword(passwordEncoder.encode("securepassword"));
        admin.setAuthorities(Set.of(roleAdmin));
        userRepository.save(admin);

    User user = new User();
        user.setUsername("bastien@example.com");
        user.setPassword(passwordEncoder.encode("tacostacos"));
        user.setAuthorities(Set.of(roleUser));
        userRepository.save(user);
}

}
