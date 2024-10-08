package com.cooperfilme.roteiros.repository;

import com.cooperfilme.roteiros.model.Roteiro;
import com.cooperfilme.roteiros.model.RoteiroStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoteiroRepository extends JpaRepository<Roteiro, Long>, JpaSpecificationExecutor<Roteiro> {
    List<Roteiro> findByStatus(RoteiroStatus status);
    List<Roteiro> findByClientEmail(String clientEmail);
    List<Roteiro> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Roteiro> findByStatusAndCreatedAtBetweenAndClientEmail(
        RoteiroStatus status,
        LocalDateTime start,
        LocalDateTime end,
        String clientEmail
    );
}
