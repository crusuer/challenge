package com.challenge.repository;

import com.challenge.entity.Starred;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StarredRepository extends JpaRepository<Starred, Long> {
    List<Starred> findByUsername(String username);
}
