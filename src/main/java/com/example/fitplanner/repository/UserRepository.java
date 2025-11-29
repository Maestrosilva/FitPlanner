package com.example.fitplanner.repository;

import com.example.fitplanner.entity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username LIKE :username")
    Optional<User> getByUsername(@Param(value = "username") String username);

    @Query("SELECT u FROM User u WHERE u.email LIKE :email")
    Optional<User> getByUsernameEmail(@Param(value = "email") String email);

    @Query("SELECT u FROM User u WHERE u.username LIKE :username AND u.password LIKE :password")
    Optional<User> getByUsernameAndPassword(@Param(value = "username") String username,
                                 @Param(value = "password") String password);

    @Query("SELECT u FROM User u WHERE u.username LIKE :email AND u.password LIKE :password")
    Optional<User> getByEmailAndPassword(@Param(value = "email") String email,
                                            @Param(value = "password") String password);
}