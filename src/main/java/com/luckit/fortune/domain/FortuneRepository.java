package com.luckit.fortune.domain;

import com.luckit.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface FortuneRepository extends JpaRepository<Fortune, Integer> {

    boolean existsByUserAndDate(User user, LocalDate date);

    Optional<Fortune> findByUserAndDate(User user, LocalDate date);

}
