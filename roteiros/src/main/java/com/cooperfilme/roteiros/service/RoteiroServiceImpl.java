package com.cooperfilme.roteiros.service;

import com.cooperfilme.roteiros.model.Roteiro;
import com.cooperfilme.roteiros.model.RoteiroStatus;
import com.cooperfilme.roteiros.model.User;
import com.cooperfilme.roteiros.repository.RoteiroRepository;

import org.hibernate.annotations.DialectOverride.OverridesAnnotation;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RoteiroServiceImpl implements RoteiroService {

    private final RoteiroRepository roteiroRepository;

    public RoteiroServiceImpl(RoteiroRepository roteiroRepository) {
        this.roteiroRepository = roteiroRepository;
    }

    @Override
    public Roteiro submitRoteiro(Roteiro roteiro) {
        roteiro.setStatus(RoteiroStatus.AGUARDANDO_ANALISE);
        return roteiroRepository.save(roteiro);
    }

    @Override
    public Roteiro updateRoteiroStatus(Long roteiroId, RoteiroStatus newStatus, User user, String justification) {
        Roteiro roteiro = roteiroRepository.findById(roteiroId)
                .orElseThrow(() -> new EntityNotFoundException("Roteiro não encontrado"));

        if (!isValidStatusTransition(roteiro.getStatus(), newStatus, user)) {
            throw new IllegalStateException("Transição de status inválida");
        }

        roteiro.setStatus(newStatus);
        roteiro.setAssignedTo(user);
        roteiro.setJustification(justification);
        return roteiroRepository.save(roteiro);
    }

    @Override
    public List<Roteiro> getRoteiroByStatus(RoteiroStatus status) {
        return roteiroRepository.findByStatus(status);
    }

    @Override
    public List<Roteiro> getRoteiroByClientEmail(String clientEmail) {
        return roteiroRepository.findByClientEmail(clientEmail);
    }

    @Override
    public List<Roteiro> getRoteiroByDateRange(LocalDateTime start, LocalDateTime end) {
        return roteiroRepository.findByCreatedAtBetween(start, end);
    }

    @Override
    public List<Roteiro> getRoteiroByStatusAndDateRangeAndClientEmail(RoteiroStatus status, LocalDateTime start, LocalDateTime end, String clientEmail) {
        return roteiroRepository.findByStatusAndCreatedAtBetweenAndClientEmail(status, start, end, clientEmail);
    }

    @Override
    public Optional<Roteiro> getRoteiroById(Long id) {
        return roteiroRepository.findById(id);
    }

    private boolean isValidStatusTransition(RoteiroStatus currentStatus, RoteiroStatus newStatus, User user) {
        // A lógica para transição de status será aplicada aqui
        return true;
    }
}
