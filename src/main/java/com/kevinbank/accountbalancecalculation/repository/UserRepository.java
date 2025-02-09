package com.kevinbank.accountbalancecalculation.repository;

import com.kevinbank.accountbalancecalculation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByName(String name);
} 