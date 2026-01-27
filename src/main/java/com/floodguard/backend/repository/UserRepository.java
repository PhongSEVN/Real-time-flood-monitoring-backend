package com.floodguard.backend.repository;

import com.floodguard.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findByRole(String role);

    List<User> findByAddressGroup(String addressGroup);

    List<User> findByPriorityLevelGreaterThanEqual(Integer priorityLevel);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
