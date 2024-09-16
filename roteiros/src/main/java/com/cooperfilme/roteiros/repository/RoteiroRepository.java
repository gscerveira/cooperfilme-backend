package com.cooperfilme.roteiros.repository;

import com.cooperfilme.roteiros.model.Roteiro;
import com.cooperfilme.roteiros.model.RoteiroStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoteiroRepository extends JpaRepository<Roteiro, Long> {
    List<Roteiro> findByStatus(RoteiroStatus status);
    List<Roteiro> findByClientEmail(String clientEmail);
    List<Roteiro> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Roteiro> findByStatusAndCreatedAtBetweenAndClientEmail(
        RoteiroStatus status,
        String start,
        String end,
        String clientEmail
    );
}
