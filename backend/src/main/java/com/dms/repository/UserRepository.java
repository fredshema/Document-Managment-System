package com.dms.repository;

import com.dms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, Long> {
    UserDetails findByUsername(String username);

    User findUserByUsername(String username);

    User findByEmail(String email);
}
