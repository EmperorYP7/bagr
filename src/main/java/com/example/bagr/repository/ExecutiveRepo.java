package com.example.bagr.repository;

import com.example.bagr.model.Executive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExecutiveRepo extends JpaRepository<Executive, Integer> {

    @Query(value = "SELECT e FROM Executive e WHERE e.username = ?1")
    Executive findByUsername(String username);
}
