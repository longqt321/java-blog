package org.example.javablog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.javablog.model.UserLog;

public interface UserLogRepository extends JpaRepository<UserLog, Long> {
    // Custom query methods can be defined here if needed
}
